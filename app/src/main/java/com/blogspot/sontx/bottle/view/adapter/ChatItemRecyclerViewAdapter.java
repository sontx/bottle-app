package com.blogspot.sontx.bottle.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.ChatChannel;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import co.dift.ui.SwipeToAction;

public class ChatItemRecyclerViewAdapter extends RecyclerView.Adapter<ChatItemRecyclerViewAdapter.ChatChannelViewHolder> {

    private final List<ChatChannel> values;

    public ChatItemRecyclerViewAdapter(List<ChatChannel> items) {
        this.values = items;
    }

    @Override
    public ChatChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chatitem, parent, false);
        return new ChatChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChatChannelViewHolder holder, int position) {
        ChatChannel item = values.get(position);

        holder.data = item;
        holder.titleView.setText(item.getRecipient());
        holder.detailView.setText(item.getLastInteractTime());
        holder.imageView.setImageURI(item.getRecipientAvatarUrl());
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    class ChatChannelViewHolder extends SwipeToAction.ViewHolder<ChatChannel> {
        TextView titleView;
        TextView detailView;
        SimpleDraweeView imageView;

        ChatChannelViewHolder(View view) {
            super(view);
            titleView = (TextView) view.findViewById(R.id.title);
            detailView = (TextView) view.findViewById(R.id.detail);
            imageView = (SimpleDraweeView) view.findViewById(R.id.image);
        }
    }
}
