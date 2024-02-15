package com.example.foodbook.SwipeCard;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;

import com.example.foodbook.Post;

public class CardStackCallback extends DiffUtil.Callback {

    private List<Post> old, baru;

    public CardStackCallback(List<Post> old, List<Post> baru) {
        this.old = old;
        this.baru = baru;
    }

    @Override
    public int getOldListSize() {
        return old.size();
    }

    @Override
    public int getNewListSize() {
        return baru.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return old.get(oldItemPosition).getImageUrl() == baru.get(newItemPosition).getImageUrl();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return old.get(oldItemPosition) == baru.get(newItemPosition);
    }
}