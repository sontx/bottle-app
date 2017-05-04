package com.blogspot.sontx.bottle.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.MessageBase;
import com.blogspot.sontx.bottle.model.bean.RoomMessage;
import com.blogspot.sontx.bottle.system.BottleContext;
import com.blogspot.sontx.bottle.system.Resource;
import com.blogspot.sontx.bottle.view.fragment.ListRoomMessageFragment;

import java.util.List;

import lombok.Getter;

public class RoomMessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TEXT = 0;
    private static final int TYPE_PHOTO = 1;
    private static final int TYPE_VIDEO = 2;

    private final ListRoomMessageFragment.OnListRoomMessageInteractionListener listener;
    @Getter
    private List<RoomMessage> values;
    private Resource resource;
    private String currentUserId;

    public RoomMessageRecyclerViewAdapter(List<RoomMessage> items, ListRoomMessageFragment.OnListRoomMessageInteractionListener listener) {
        values = items;
        this.listener = listener;

        BottleContext bottleContext = App.getInstance().getBottleContext();
        resource = bottleContext.getResource();
        currentUserId = bottleContext.getCurrentBottleUser().getUid();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_TEXT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_room_message_text, parent, false);
            return new MessageAdapterHelper.TextViewHolder(view);
        } else if (viewType == TYPE_PHOTO) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_room_message_photo, parent, false);
            return new MessageAdapterHelper.PhotoViewHolder(view);
        } else if (viewType == TYPE_VIDEO) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_room_message_video, parent, false);
            return new MessageAdapterHelper.VideoViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        RoomMessage roomMessage = values.get(position);
        if (MessageBase.PHOTO.equalsIgnoreCase(roomMessage.getType()))
            return TYPE_PHOTO;
        if (MessageBase.VIDEO.equalsIgnoreCase(roomMessage.getType()))
            return TYPE_VIDEO;
        return TYPE_TEXT;
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        RoomMessage roomMessage = values.get(position);

        // apply for all message types
        final MessageAdapterHelper.TextViewHolder textViewHolder = (MessageAdapterHelper.TextViewHolder) holder;

        MessageAdapterHelper.bindMessageToTextViewHolder(roomMessage, textViewHolder, resource, currentUserId, listener);

        textViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onListRoomMessageInteraction((RoomMessage) textViewHolder.item);
                }
            }
        });

        // message type is photo
        if (holder.getItemViewType() == TYPE_PHOTO) {
            MessageAdapterHelper.bindMessageToPhotoViewHolder((MessageAdapterHelper.PhotoViewHolder) holder, roomMessage, resource);
        } else if (holder.getItemViewType() == TYPE_VIDEO) {
            MessageAdapterHelper.bindMessageToVideoViewHolder((MessageAdapterHelper.VideoViewHolder) holder, roomMessage, resource);
        }
    }
}
