package com.projects.finn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.projects.finn.databinding.RecyclerCommentBinding;
import com.projects.finn.models.Comment;
import com.projects.finn.utils.CommentDiffUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {
    @Inject
    RequestManager glide;
    private final Context context;
    private ArrayList<Comment> comments;

    public CommentsAdapter(Context context, ArrayList<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RecyclerCommentBinding binding;

        public MyViewHolder(RecyclerCommentBinding b) {
            super(b.getRoot());
            this.binding = b;
        }

        public void bind(Comment comment) {
            if (comment.getUser_image() != null) {
                glide.load(comment.getUser_image()).into(binding.userIcon);
            }
            binding.tvUserName.setText(comment.getUser_name());
            binding.tvContent.setText(comment.getContent());

            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("LLLL yyyy", Locale.getDefault());
            String date = DATE_FORMAT.format(comment.getDate());
            String capsDate = " - " + date.substring(0, 1).toUpperCase() + date.substring(1);
            binding.tvDate.setText(capsDate);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new MyViewHolder(RecyclerCommentBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setComments(ArrayList<Comment> comments) {
        CommentDiffUtil util = new CommentDiffUtil(this.comments, comments);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(util);
        this.comments = comments;
        result.dispatchUpdatesTo(this);
    }

    public void updateComment(Comment comment) {
        comments.set(comments.indexOf(comment), comment);
        notifyItemChanged(comments.indexOf(comment));
    }
}
