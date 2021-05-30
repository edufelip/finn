package com.example.finn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finn.R;
import com.example.finn.data.Post;

import java.util.ArrayList;

public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.MyViewHolder> {
    ArrayList<Post> posts = new ArrayList<Post>();
    Context context;

    public FeedRecyclerAdapter(Context context, ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_post, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedRecyclerAdapter.MyViewHolder holder, int position) {
        holder.postTitle.setText(posts.get(position).getTitle());
        holder.postSource.setText("Posted by: " + posts.get(position).getUserName());
        holder.postCommunity.setText(posts.get(position).getCommunityName());
        holder.postDescription.setText(posts.get(position).getDescription());
        holder.likesCount.setText(String.valueOf(posts.get(position).getLikes()));
        holder.commentsCount.setText(String.valueOf(posts.get(position).getComments()));
        //holder.postImage.setImageResource(posts.get(position).getImgSource());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView postCommunity;
        TextView postSource;
        TextView postTitle;
        TextView postDescription;
        TextView likesCount;
        TextView commentsCount;
        ImageView postImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            postCommunity = itemView.findViewById(R.id.post_community);
            postSource = itemView.findViewById(R.id.post_source);
            postTitle = itemView.findViewById(R.id.post_title);
            postDescription = itemView.findViewById(R.id.post_description);
            postImage = itemView.findViewById(R.id.post_image);
            likesCount = itemView.findViewById(R.id.likes_count);
            commentsCount = itemView.findViewById(R.id.comments_count);
        }
    }
}
