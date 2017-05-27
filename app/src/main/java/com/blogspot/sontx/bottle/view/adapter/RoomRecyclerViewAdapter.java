package com.blogspot.sontx.bottle.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.Room;
import com.blogspot.sontx.bottle.system.Resource;
import com.blogspot.sontx.bottle.utils.StringUtils;
import com.blogspot.sontx.bottle.view.fragment.ListRoomFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import lombok.Getter;

public class RoomRecyclerViewAdapter extends RecyclerView.Adapter<RoomRecyclerViewAdapter.ViewHolder> {

    private final ListRoomFragment.OnListRoomInteractionListener listener;
    @Getter
    private List<Room> values;
    private int avatarSize;
    private Resource resource;
    private int roomId = -1;

    public RoomRecyclerViewAdapter(List<Room> items, ListRoomFragment.OnListRoomInteractionListener listener) {
        values = items;
        this.listener = listener;
        resource = App.getInstance().getBottleContext().getResource();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Room room = values.get(position);

        holder.item = room;
        holder.titleView.setText(room.getName());
        holder.descriptionView.setText(room.getDescription());

        if (!StringUtils.isEmpty(room.getPhotoUrl())) {
            String url = resource.absoluteUrl(room.getPhotoUrl());
            Picasso.with(holder.root.getContext()).load(url).resize(avatarSize, avatarSize).centerCrop().into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_default_room);
        }

        holder.onlineView.setVisibility(room.getId() == roomId ? View.VISIBLE : View.INVISIBLE);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onListRoomInteraction(holder.item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public void setAvatarSize(int avatarSize) {
        this.avatarSize = avatarSize;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View root;
        final View onlineView;
        final ImageView imageView;
        final TextView titleView;
        final TextView descriptionView;

        Room item;

        ViewHolder(View view) {
            super(view);
            root = view;
            titleView = ButterKnife.findById(view, R.id.title_view);
            descriptionView = ButterKnife.findById(view, R.id.description_view);
            imageView = ButterKnife.findById(view, R.id.image_view);
            onlineView = ButterKnife.findById(view, R.id.online_view);
        }
    }
}
