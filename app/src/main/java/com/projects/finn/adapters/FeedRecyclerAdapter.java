package com.projects.finn.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.projects.finn.R;
import com.projects.finn.databinding.RecyclerPostBinding;
import com.projects.finn.models.Community;
import com.projects.finn.models.Post;
import com.projects.finn.ui.activities.CommunityActivity;
import com.projects.finn.ui.activities.PostActivity;
import com.projects.finn.utils.PostDiffUtil;
import com.projects.finn.utils.RemoteConfigUtils;
import com.projects.finn.utils.extensions.GlideUtils;

import java.util.ArrayList;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.inject.Inject;


public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.MyViewHolder> {
    @Inject
    RemoteConfigUtils remoteConfigUtils;
    @Inject
    GlideUtils glideUtils;
    private ArrayList<Post> posts;
    private final Context context;
    private final RecyclerClickListener recyclerClickListener;

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
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RecyclerPostBinding binding;
        Dialog userPopup;
        RecyclerClickListener recyclerClickListener;

        public MyViewHolder(RecyclerPostBinding b, RecyclerClickListener recyclerClickListener) {
            super(b.getRoot());
            this.binding = b;
            this.recyclerClickListener = recyclerClickListener;
            setupClickListeners(itemView);
            itemView.setOnClickListener(this);
            userPopup = new Dialog(itemView.getContext());
        }

        public void bind(Post post) {
            String source = context.getResources().getString(R.string.posted_by) + " " + post.getUser_name();
            binding.postContent.setText(post.getContent());
            binding.postSource.setText(source);
            binding.postCommunity.setText(post.getCommunity_title());
            binding.likesCount.setText(String.valueOf(post.getLikes_count()));
            binding.commentsCount.setText(String.valueOf(post.getComments_count()));
            binding.likeButton.setChecked(false);
            if (post.isLiked()) {
                binding.likeButton.setChecked(true);
            }

            binding.postImage.setImageDrawable(null);
            binding.communityPictureIcon.setImageDrawable(null);
            glideUtils.glideClear(binding.communityPictureIcon);
            glideUtils.loadFromServer(post.getCommunity_image(), binding.communityPictureIcon);
            String image = post.getImage();
            if (image != null) {
                binding.postImage.layout(0, 0, 0, 0);
                glideUtils.glideClear(binding.postImage);
                glideUtils.loadFromServer(image, binding.postImage);
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
                        case (R.id.reportOption):
                            Toast.makeText(itemView.getContext(), context.getResources().getString(R.string.not_available_yet), Toast.LENGTH_SHORT).show();
                            break;
                        case (R.id.hideOption):
                            recyclerClickListener.onDeleteClick(getAbsoluteAdapterPosition());
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
            binding.postSource.setOnClickListener(v -> {
//                openUserPopup();
            });
            binding.likeButton.setOnClickListener(v -> {
                if (binding.likeButton.isChecked()) {
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

        public void openPostActivity() {
            Intent intent = new Intent(itemView.getContext(), PostActivity.class);
            itemView.getContext().startActivity(intent);
        }

        public void likePost() {
            int likes = Integer.parseInt(binding.likesCount.getText().toString()) + 1;
            binding.likesCount.setText(String.valueOf(likes));
            recyclerClickListener.onLikePost(getAbsoluteAdapterPosition());
        }

        public void dislikePost() {
            int likes = Integer.parseInt(binding.likesCount.getText().toString()) - 1;
            binding.likesCount.setText(String.valueOf(likes));
            recyclerClickListener.onDislikePost(getAbsoluteAdapterPosition());
        }

        public void sharePost() {
            // Not yet implemented
            Toast.makeText(itemView.getContext(), context.getResources().getString(R.string.not_available_yet), Toast.LENGTH_SHORT).show();
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

    public void updatePostActivityResult(Post post) {
        Post result = posts.stream()
            .filter(element -> element.getId() == post.getId())
            .collect(toSingleton());

        int index = posts.indexOf(result);
        if (post.getUser_id() != null && post.getUser_id().equals("-2")) {
            posts.remove(result);
            notifyItemRemoved(index);
            return;
        }

        posts.set(index, post);
        notifyItemChanged(index);
    }

    public <T> Collector<T, ?, T> toSingleton() {
        return Collectors.collectingAndThen(
            Collectors.toList(),
            list -> {
                if (list.size() != 1) {
                    throw new IllegalStateException();
                }
                return list.get(0);
            }
        );
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
