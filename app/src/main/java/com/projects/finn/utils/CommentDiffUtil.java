package com.projects.finn.utils;

import com.projects.finn.models.Comment;

import java.util.List;

public class CommentDiffUtil extends androidx.recyclerview.widget.DiffUtil.Callback {
        List<Comment> oldList;
        List<Comment> newList;

        public CommentDiffUtil(List<Comment> oldList, List<Comment> newList) {
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
            if(oldList.get(oldItemPosition).getUser_name() != newList.get(newItemPosition).getUser_name()) return false;
            if(oldList.get(oldItemPosition).getUser_image() != newList.get(newItemPosition).getUser_image()) return false;
            if(oldList.get(oldItemPosition).getDate() != newList.get(newItemPosition).getDate()) return false;
            return true;
        }
}
