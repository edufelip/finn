package com.edufelip.finn.utils;

import com.edufelip.finn.domain.models.Post;

import java.util.List;

public class PostDiffUtil extends androidx.recyclerview.widget.DiffUtil.Callback {
    List<Post> oldList;
    List<Post> newList;

    public PostDiffUtil(List<Post> oldList, List<Post> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        if(oldList.get(oldItemPosition).getContent() != newList.get(newItemPosition).getContent()) return false;
        if(oldList.get(oldItemPosition).getImage() != newList.get(newItemPosition).getImage()) return false;
        if(oldList.get(oldItemPosition).getDate() != newList.get(newItemPosition).getDate()) return false;
        return true;
    }
}
