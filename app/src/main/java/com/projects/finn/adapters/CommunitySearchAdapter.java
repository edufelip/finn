package com.projects.finn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.projects.finn.BuildConfig;
import com.projects.finn.databinding.RecyclerTrendingBinding;
import com.projects.finn.models.Community;
import com.projects.finn.models.Post;

import java.util.ArrayList;


public class CommunitySearchAdapter extends RecyclerView.Adapter<CommunitySearchAdapter.MyViewHolder> {
    private RequestManager glide;
    private ArrayList<Community> communities;
    private Context context;
    private RecyclerClickListener recyclerClickListener;

    public CommunitySearchAdapter(Context context, ArrayList<Community> communities, RecyclerClickListener recyclerClickListener, RequestManager glide) {
        this.context = context;
        this.communities = communities;
        this.recyclerClickListener = recyclerClickListener;
        this.glide = glide;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RecyclerTrendingBinding binding;
        RecyclerClickListener recyclerClickListener;

        public MyViewHolder(RecyclerTrendingBinding b, RecyclerClickListener recyclerClickListener) {
            super(b.getRoot());
            binding = b;
            setupClickListeners();

            this.recyclerClickListener = recyclerClickListener;
            itemView.setOnClickListener(this);
        }

        public void binding(Community community) {
            String title = community.getTitle();
            String description = community.getDescription();
            int count = community.getSubscribersCount();
            String subscribers = String.valueOf(count) + " Subscribers";
            binding.tvTitle.setText(title);
            binding.tvAbout.setText(description);
            binding.tvFollowers.setText(subscribers);
            String image = community.getImage();
            glide.load(BuildConfig.BACKEND_IP + "/" + image).into(binding.communityIcon);
        }

        public void setupClickListeners() {

        }

        @Override
        public void onClick(View view) {
            recyclerClickListener.onItemClick(getAbsoluteAdapterPosition());
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new MyViewHolder(RecyclerTrendingBinding.inflate(inflater, parent, false), recyclerClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding(communities.get(position));
    }

    @Override
    public int getItemCount() {
        return communities.size();
    }

    public void setCommunities(ArrayList<Community> communities) {
        this.communities = communities;
        notifyDataSetChanged();
    }

    public void updateCommunity(Community community) {
        communities.set(communities.indexOf(community), community);
        notifyItemChanged(communities.indexOf(community));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface RecyclerClickListener {
        void onItemClick(int position);
    }
}
