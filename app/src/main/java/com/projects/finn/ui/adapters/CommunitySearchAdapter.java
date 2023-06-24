package com.projects.finn.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projects.finn.R;
import com.projects.finn.databinding.RecyclerTrendingBinding;
import com.projects.finn.domain.models.Community;
import com.projects.finn.utils.RemoteConfigUtils;
import com.projects.finn.utils.extensions.GlideUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collector;
import java.util.stream.Collectors;


public class CommunitySearchAdapter extends RecyclerView.Adapter<CommunitySearchAdapter.MyViewHolder> {
    private final GlideUtils glideUtils;
    private ArrayList<Community> communities;
    private final Context context;
    private final RecyclerClickListener recyclerClickListener;

    public CommunitySearchAdapter(
        Context context,
        ArrayList<Community> communities,
        RecyclerClickListener recyclerClickListener,
        GlideUtils glideUtils) {
        this.context = context;
        this.communities = communities;
        this.recyclerClickListener = recyclerClickListener;
        this.glideUtils = glideUtils;
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
            String subscribers = count + " " + context.getResources().getString(R.string.subscriber_s);
            binding.tvTitle.setText(title);
            binding.tvAbout.setText(description);
            binding.tvFollowers.setText(subscribers);
            String image = community.getImage();
            if (image != null)
                glideUtils.loadFromServer(image, binding.communityIcon);
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

    public void updateCommunityActivityResult(Community community) {
        Community result = communities.stream()
            .filter(element -> element.getId() == community.getId())
            .collect(toSingleton());
        int index = communities.indexOf(result);
        if (communities.get(index).getSubscribersCount() != community.getSubscribersCount()) {
            communities.set(index, community);
            communities.sort(Comparator.comparing(Community::getSubscribersCount).reversed());
            notifyDataSetChanged();
        }
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
