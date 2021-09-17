package com.projects.finn.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.projects.finn.BuildConfig;
import com.projects.finn.R;
import com.projects.finn.databinding.RecyclerPostBinding;
import com.projects.finn.models.Community;
import com.projects.finn.ui.activities.CommunityActivity;
import com.projects.finn.ui.activities.PostActivity;
import com.projects.finn.models.Post;
import com.projects.finn.utils.PostDiffUtil;

import java.util.ArrayList;

import javax.inject.Inject;


public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.MyViewHolder> {
    private RequestManager glide;
    private ArrayList<Post> posts;
    private Context context;
    private RecyclerClickListener recyclerClickListener;

    public FeedRecyclerAdapter(Context context, ArrayList<Post> posts, RecyclerClickListener recyclerClickListener, RequestManager glide) {
        this.context = context;
        this.posts = posts;
        this.recyclerClickListener = recyclerClickListener;
        this.glide = glide;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new MyViewHolder(RecyclerPostBinding.inflate(inflater, parent, false), recyclerClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedRecyclerAdapter.MyViewHolder holder, int position) {
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        RecyclerPostBinding binding;
        Dialog userPopup;
        Boolean isLikeButtonClicked;
        RecyclerClickListener recyclerClickListener;

        public MyViewHolder(RecyclerPostBinding b, RecyclerClickListener recyclerClickListener) {
            super(b.getRoot());
            binding = b;
            setupClickListeners(itemView);
            this.recyclerClickListener = recyclerClickListener;
            itemView.setOnClickListener(this);
            userPopup = new Dialog(itemView.getContext());
        }

        public void bind(Post post) {
            String source = "Posted by: " + post.getUser_name();
            isLikeButtonClicked = post.isLiked();
            binding.postContent.setText(post.getContent());
            binding.postSource.setText(source);
            binding.postCommunity.setText(post.getCommunity_title());
            binding.likesCount.setText(String.valueOf(post.getLikes_count()));
            binding.commentsCount.setText(String.valueOf(post.getComments_count()));
            binding.likeButton.setChecked(false);
            if(post.isLiked()) {
                binding.likeButton.setChecked(true);
                this.isLikeButtonClicked = true;
            }

            binding.postImage.setImageDrawable(null);
            binding.communityPictureIcon.setImageDrawable(null);
            glide.clear(binding.communityPictureIcon);
            glide.load(BuildConfig.BACKEND_IP + "/" + post.getCommunity_image()).into(binding.communityPictureIcon);
            String image = post.getImage();
            if(image != null) {
                binding.postImage.layout(0,0,0,0);
                glide.clear(binding.postImage);
                glide.load(BuildConfig.BACKEND_IP + "/" + image).into(binding.postImage);
            }
        }

        @Override
        public void onClick(View v) {
            recyclerClickListener.onItemClick(getAbsoluteAdapterPosition());
        }

        public void setupClickListeners(View itemView) {
            binding.textViewOptions.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(itemView.getContext(), binding.textViewOptions);
                popupMenu.inflate(R.menu.recycler_options_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case (R.id.saveOption):
                            // send request to save post
                            break;
                        case (R.id.hideOption):
                            // send request
                            recyclerClickListener.onDeleteClick(getAbsoluteAdapterPosition());
                            break;
                        case (R.id.reportOption):
                            break;
                        default:
                            return false;
                    }
                    return true;
                });
                popupMenu.show();
            });

            binding.communityPictureIcon.setOnClickListener(v -> openCommunityActivity());
            binding.postCommunity.setOnClickListener(v -> openCommunityActivity());
            binding.postSource.setOnClickListener(v -> openUserPopup());
            binding.likeButton.setOnClickListener(v -> {
                if(!this.isLikeButtonClicked) {
                    likePost();
                } else {
                    dislikePost();
                }
            });
            binding.commentButton.setOnClickListener(v -> openPostActivity());
            binding.commentsCount.setOnClickListener(v -> openPostActivity());
            binding.shareButton.setOnClickListener(v -> sharePost());
            binding.shareText.setOnClickListener(v -> sharePost());
        }

        public void openCommunityActivity() {
            Intent intent = new Intent(itemView.getContext(), CommunityActivity.class);
            Post post = posts.get(getAbsoluteAdapterPosition());
            Community community = new Community();
            community.setId(post.getCommunity_id());
            community.setTitle(post.getCommunity_title());
            community.setImage(post.getCommunity_image());
            intent.putExtra("community", community);
            itemView.getContext().startActivity(intent);
        }

        public void openUserPopup() {
            TextView popupClose;
            userPopup.setContentView(R.layout.user_popup);
            popupClose = userPopup.findViewById(R.id.close_popup);
            popupClose.setOnClickListener(v -> userPopup.dismiss());
            userPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            userPopup.show();
        }

        public void openPostActivity() {
            Intent intent = new Intent(itemView.getContext(), PostActivity.class);
            // put extras
            itemView.getContext().startActivity(intent);
        }

        public void likePost() {
            isLikeButtonClicked = !isLikeButtonClicked;
            int likes = Integer.parseInt(binding.likesCount.getText().toString()) + 1;
            binding.likesCount.setText(String.valueOf(likes));
            recyclerClickListener.onLikePost(getAbsoluteAdapterPosition());
        }

        public void dislikePost() {
            isLikeButtonClicked = !isLikeButtonClicked;
            int likes = Integer.parseInt(binding.likesCount.getText().toString()) - 1;
            binding.likesCount.setText(String.valueOf(likes));
            recyclerClickListener.onDislikePost(getAbsoluteAdapterPosition());
        }

        public void sharePost() {
            // Not yet implemented
            Toast.makeText(itemView.getContext(), "Not available yet", Toast.LENGTH_SHORT).show();
        }
    }

    public void setPosts(ArrayList<Post> posts) {
        PostDiffUtil util = new PostDiffUtil(this.posts, posts);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(util);
        this.posts = posts;
        result.dispatchUpdatesTo(this);
    }

    public void updatePost(Post post) {
        posts.set(posts.indexOf(post), post);
        notifyItemChanged(posts.indexOf(post));
    }

    public interface RecyclerClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
        void onLikePost(int position);
        void onDislikePost(int position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
