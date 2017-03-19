package com.blogspot.sontx.bottle.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.ChatChannelInfo;
import com.blogspot.sontx.bottle.utils.DateTimeUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import co.dift.ui.SwipeToAction;

public class ListChatChannelRecyclerViewAdapter extends RecyclerView.Adapter<ListChatChannelRecyclerViewAdapter.ChatChannelViewHolder> {

    private final List<ChatChannelInfo> values;

    public void add(ChatChannelInfo chatChannelInfo) {
        values.add(chatChannelInfo);
    }

    public ListChatChannelRecyclerViewAdapter() {
        this.values = new ArrayList<>();
    }

    @Override
    public ChatChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chatchannel_item, parent, false);
        return new ChatChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChatChannelViewHolder holder, int position) {
        ChatChannelInfo item = values.get(position);

        holder.data = item;
        holder.titleView.setText(item.getRecipientDisplayName());
        holder.detailView.setText(DateTimeUtils.getTimestamp(item.getLastActiveTime()));
        holder.imageView.setImageURI(item.getRecipientAvatarUrl());
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    class ChatChannelViewHolder extends SwipeToAction.ViewHolder<ChatChannelInfo> {
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
