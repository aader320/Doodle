package com.example.foodbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.foodbook.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set view binding
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_sign_up)

        auth = Firebase.auth

        binding.buttonSignUp.setOnClickListener(){
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            println("ONCLICK BUTTON SET")

            if(checkAllField()) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener() {

                    println("ALL FIELDS CHECKED")
                    // if account creation is successful. auto signout
                    if(it.isSuccessful) {
                        auth.signOut()
                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                    }
                    else {
                        // account creation failed
                        Log.e(">> Error: ", it.exception.toString())
                        Toast.makeText(this, "Unsuccessful account creation", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun checkAllField(): Boolean {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val checkpassword = binding.etConfirmPassword.text.toString()

        if(email == ""){
            binding.textInputLayoutEmail.error = "This is a required field"
            return false
        }

        val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (!email.matches(emailRegex.toRegex())) {
            binding.textInputLayoutEmail.error = "Check the email format"
            return false
        }

        if(password == "") {
            binding.textInputLayoutPassword.error = "This is a required field"
            binding.textInputLayoutConfirmPassword.errorIconDrawable = null
            return false
        }

        if(password.length <= 6) {
            binding.textInputLayoutPassword.error = "Password should be at least 6 characters long"
            binding.textInputLayoutConfirmPassword.errorIconDrawable = null
            return false
        }

        if(checkpassword == "") {
            binding.textInputLayoutConfirmPassword.error = "This is a required field"
            binding.textInputLayoutConfirmPassword.errorIconDrawable = null
            return false
        }

        if(password != checkpassword) {
            binding.textInputLayoutPassword.error = "Passwords do not match"
            return false
        }

        return true
    }
}