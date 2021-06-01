package com.example.finn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finn.R;
import com.example.finn.data.Post;

import java.util.ArrayList;

public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.MyViewHolder> {
    private ArrayList<Post> posts;
    private Context context;
    private RecyclerClickListener recyclerClickListener;

    public FeedRecyclerAdapter(Context context, ArrayList<Post> posts, RecyclerClickListener recyclerClickListener) {
        this.context = context;
        this.posts = posts;
        this.recyclerClickListener = recyclerClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_post, parent,false);
        return new MyViewHolder(view, recyclerClickListener);
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

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView postCommunity;
        TextView postSource;
        TextView postTitle;
        TextView postDescription;
        TextView likesCount;
        TextView commentsCount;
        ImageView postImage;
        TextView optionsButton;
        RecyclerClickListener recyclerClickListener;

        public MyViewHolder(@NonNull View itemView, RecyclerClickListener recyclerClickListener) {
            super(itemView);
            postCommunity = itemView.findViewById(R.id.post_community);
            postSource = itemView.findViewById(R.id.post_source);
            postTitle = itemView.findViewById(R.id.post_title);
            postDescription = itemView.findViewById(R.id.post_description);
            postImage = itemView.findViewById(R.id.post_image);
            likesCount = itemView.findViewById(R.id.likes_count);
            commentsCount = itemView.findViewById(R.id.comments_count);
            optionsButton = itemView.findViewById(R.id.textViewOptions);

            this.recyclerClickListener = recyclerClickListener;
            itemView.setOnClickListener(this);

            optionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(itemView.getContext(), optionsButton);
                    popupMenu.inflate(R.menu.recycler_options_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getTitle().toString()) {
                                case "Save":
                                    Toast.makeText(itemView.getContext(), "SAVE BUTTON CLICKED", Toast.LENGTH_SHORT).show();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popupMenu.show();
                }
            });
        }

        @Override
        public void onClick(View v) {
            recyclerClickListener.onItemClick(getBindingAdapterPosition());
        }
    }

    public interface RecyclerClickListener {
        void onItemClick(int position);
    }
}
