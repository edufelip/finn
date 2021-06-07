package com.example.finn.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finn.R;
import com.example.finn.activities.CommunityActivity;
import com.example.finn.activities.PostActivity;
import com.example.finn.data.Post;
import com.google.android.material.checkbox.MaterialCheckBox;

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
        ConstraintLayout recyclerLayout;
        TextView postCommunity;
        TextView postSource;
        TextView postTitle;
        TextView postDescription;
        TextView likesCount;
        TextView commentsCount;
        TextView optionsButton;
        TextView shareText;
        ImageView postImage;
        ImageView postIcon;
        MaterialCheckBox likeButton;
        MaterialCheckBox dislikeButton;
        Button commentsButton;
        Button shareButton;
        Dialog userPopup;
        Boolean isLikeButtonClicked;
        Boolean isDislikeButtonClicked;
        RecyclerClickListener recyclerClickListener;

        public MyViewHolder(@NonNull View itemView, RecyclerClickListener recyclerClickListener) {
            super(itemView);
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
            recyclerLayout = itemView.findViewById(R.id.recycler_layout);
            postCommunity = itemView.findViewById(R.id.post_community);
            postSource = itemView.findViewById(R.id.post_source);
            postTitle = itemView.findViewById(R.id.post_title);
            postDescription = itemView.findViewById(R.id.post_description);
            postImage = itemView.findViewById(R.id.post_image);
            postIcon = itemView.findViewById(R.id.community_picture_icon);
            likesCount = itemView.findViewById(R.id.likes_count);
            likeButton = itemView.findViewById(R.id.like_button);
            dislikeButton = itemView.findViewById(R.id.dislike_button);
            optionsButton = itemView.findViewById(R.id.textViewOptions);
            commentsCount = itemView.findViewById(R.id.comments_count);
            commentsButton = itemView.findViewById(R.id.comment_button);
            shareButton = itemView.findViewById(R.id.share_button);
            shareText = itemView.findViewById(R.id.share_text);
            // Pull from db
            isLikeButtonClicked = false;
            isDislikeButtonClicked = false;
            //
        }

        public void setupClickListeners(View itemView) {
            optionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(itemView.getContext(), optionsButton);
                    popupMenu.inflate(R.menu.recycler_options_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
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
                        }
                    });
                    popupMenu.show();
                }
            });

            postIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCommunityActivity();
                }
            });

            postCommunity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCommunityActivity();
                }
            });

            postSource.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openUserPopup();
                }
            });

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    likePost();
                }
            });

            dislikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dislikePost();
                }
            });

            commentsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPostActivity();
                }
            });

            commentsCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPostActivity();
                }
            });

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharePost();
                }
            });

            shareText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharePost();
                }
            });

            likesCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
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
            popupClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userPopup.dismiss();
                }
            });
            userPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            userPopup.show();
        }

        public void openPostActivity() {
            Intent intent = new Intent(itemView.getContext(), PostActivity.class);
            // put extras
            itemView.getContext().startActivity(intent);
        }

        public void likePost() {
            if(dislikeButton.isChecked()) {
                dislikeButton.toggle();
            }
            isLikeButtonClicked = !isLikeButtonClicked;
            // send request
        }

        public void dislikePost() {
            if(likeButton.isChecked()) {
                likeButton.toggle();
            }
            isDislikeButtonClicked = !isDislikeButtonClicked;
            // send request
        }

        public void sharePost() {
            // Not yet implemented
            Toast.makeText(itemView.getContext(), "Not available yet", Toast.LENGTH_SHORT).show();
        }
    }

    public interface RecyclerClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }
}
