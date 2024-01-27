package com.example.foodbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.foodbook.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set view binding
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_sign_in)

        // use to remove action bar (why?)
        supportActionBar?.hide()
        auth = Firebase.auth

        binding.buttonSignIn.setOnClickListener() {
            if(checkAllField()) {
                // all signin fields are satisfied
                val email = binding.inputEmail.text.toString()
                val password = binding.inputPassword.text.toString()
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener() {
                    if(it.isSuccessful) {
                        // if the signin is successful
                        Toast.makeText(this, "Sign in Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()    // destroy the activity
                    }
                    else {
                        // signin is not successful
                        Log.e(">> ERROR: ", it.exception.toString())
                        Toast.makeText(this, "Wrong Credentials!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private fun checkAllField(): Boolean {
        val email = binding.inputEmail.text.toString()
        val password = binding.inputPassword.text.toString()

        if(email == ""){
            binding.textInputLayoutEmailSignin.error = "This is a required field"
            return false
        }

        val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (!email.matches(emailRegex.toRegex())) {
            binding.textInputLayoutEmailSignin.error = "Check the email format"
            return false
        }

        if(password == "") {
            binding.textInputLayoutPasswordSignin.error = "This is a required field"
            binding.textInputLayoutPasswordSignin.errorIconDrawable = null
            return false
        }

        if(password.length <= 6) {
            binding.textInputLayoutPasswordSignin.error = "Password should be at least 6 characters long"
            binding.textInputLayoutPasswordSignin.errorIconDrawable = null
            return false
        }

        return true
    }
}