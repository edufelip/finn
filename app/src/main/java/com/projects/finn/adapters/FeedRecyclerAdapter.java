package com.projects.finn.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projects.finn.R;
import com.projects.finn.databinding.RecyclerPostBinding;
import com.projects.finn.ui.activities.CommunityActivity;
import com.projects.finn.ui.activities.PostActivity;
import com.projects.finn.models.Post;

import java.util.ArrayList;
import java.util.List;

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
        return new MyViewHolder(RecyclerPostBinding.inflate(inflater, parent, false), recyclerClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedRecyclerAdapter.MyViewHolder holder, int position) {
        String source = "Posted by: " + posts.get(position).getUser_name();
        holder.binding.postContent.setText(posts.get(position).getContent());
        holder.binding.postSource.setText(source);
        holder.binding.postCommunity.setText(posts.get(position).getCommunity_name());
        holder.binding.likesCount.setText(String.valueOf(posts.get(position).getLikes_count()));
        holder.binding.commentsCount.setText(String.valueOf(posts.get(position).getComments_count()));
        //holder.postImage.setImageResource(posts.get(position).getImgSource());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        RecyclerPostBinding binding;
        Dialog userPopup;
        Boolean isLikeButtonClicked;
        Boolean isDislikeButtonClicked;
        RecyclerClickListener recyclerClickListener;

        public MyViewHolder(RecyclerPostBinding b, RecyclerClickListener recyclerClickListener) {
            super(b.getRoot());
            binding = b;
            initializeComponents();
            setupClickListeners(itemView);

            this.recyclerClickListener = recyclerClickListener;
            itemView.setOnClickListener(this);
            userPopup = new Dialog(itemView.getContext());
        }

        @Override
        public void onClick(View v) {
            recyclerClickListener.onItemClick(getAbsoluteAdapterPosition());
        }

        public void initializeComponents() {
            // Pull from db
            isLikeButtonClicked = false;
            isDislikeButtonClicked = false;
            //
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
            binding.likeButton.setOnClickListener(v -> likePost());
            binding.dislikeButton.setOnClickListener(v -> dislikePost());
            binding.commentButton.setOnClickListener(v -> openPostActivity());
            binding.commentsCount.setOnClickListener(v -> openPostActivity());
            binding.shareButton.setOnClickListener(v -> sharePost());
            binding.shareText.setOnClickListener(v -> sharePost());
            binding.likesCount.setOnClickListener(v -> {

            });
        }

        public void openCommunityActivity() {
            Intent intent = new Intent(itemView.getContext(), CommunityActivity.class);
            // put extras
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
            if(binding.dislikeButton.isChecked()) {
                binding.dislikeButton.toggle();
            }
            isLikeButtonClicked = !isLikeButtonClicked;
            // send request
        }

        public void dislikePost() {
            if(binding.likeButton.isChecked()) {
                binding.likeButton.toggle();
            }
            isDislikeButtonClicked = !isDislikeButtonClicked;
            // send request
        }

        public void sharePost() {
            // Not yet implemented
            Toast.makeText(itemView.getContext(), "Not available yet", Toast.LENGTH_SHORT).show();
        }
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    public void updatePost(Post post) {
        posts.set(posts.indexOf(post), post);
        notifyItemChanged(posts.indexOf(post));
    }

    public interface RecyclerClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }
}
