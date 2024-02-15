package com.example.foodbook.SwipeCard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodbook.Post;
import com.example.foodbook.R;
import com.example.foodbook.postsViewModel;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;

import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.Deferred;
import kotlin.coroutines.Continuation;

public class SwipeCard extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private CardStackLayoutManager manager;
    private CardSwipeAdapter adapter;
    private postsViewModel viewmodel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_card);

        CardStackView cardStackView = findViewById(R.id.card_stack_view);
        manager = new CardStackLayoutManager(this, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
//                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
//                Log.d(TAG, String.valueOf(manager.getTopPosition()));
//                Log.d(TAG, String.valueOf(adapter.getItemCount()));
//                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                if (direction == Direction.Right) {
//                    Toast.makeText(SwipeCard.this, "Direction Right", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Top) {
//                    Toast.makeText(SwipeCard.this, "Direction Top", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Left) {
//                    Toast.makeText(SwipeCard.this, "Direction Left", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Bottom) {
//                    Toast.makeText(SwipeCard.this, "Direction Bottom", Toast.LENGTH_SHORT).show();
                }

                // Paginating
                if (manager.getTopPosition() == adapter.getItemCount() - 2) {
                    paginate();
                }
            }

            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }
        });

        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());

        try
        {
            viewmodel = new postsViewModel(getApplication());
            //viewmodel = ViewModelProvider(this).get(postsViewModel.class);

            while(viewmodel == null)  {
                Log.d("test", "initializing viewmodel");
            }
            Log.d("test", "viewmodel intiialized");

            adapter = new CardSwipeAdapter(addList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }

    private void paginate() {
        Log.d(TAG, "WHY ARE YOU NOT PAGINATING");
        List<Post> old = adapter.getItems();
        List<Post> baru = new ArrayList<>(addList());
        CardStackCallback callback = new CardStackCallback(old, baru);
        DiffUtil.DiffResult hasil = DiffUtil.calculateDiff(callback);
        adapter.setItems(baru);
//        RecyclerView.Adapter.notifyItemRangeInserted(9,9);
        hasil.dispatchUpdatesTo(adapter);
        adapter.notifyDataSetChanged();
    }

    public interface DeferredCallback<T> {
        void onComplete(Deferred<T> deferred);
    }

    private List<Post> addList()
    {
        List<Post> items = new ArrayList<>();
        List<Post> test =  new ArrayList<>();

        test = viewmodel.getAllPosts();

        Log.d("test", "viewmodelsize" + String.valueOf(test.size()));
        items.add(new Post(1, "testsetests1", "test2", "1232131", "test@gmail", 12321321));
        //items.add(viewmodel.getAllPosts().get(0));
//        items.add(new ItemModel(R.drawable.download, "jerry","22","gay"));
//        items.add(new ItemModel(R.drawable.download, "jerry","22","gay"));
//        items.add(new ItemModel(R.drawable.download, "jerry","22","dd"));

        return items;
    }
}



