package com.blogspot.sontx.bottle.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.MessageBase;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.RoomMessage;
import com.blogspot.sontx.bottle.system.BottleContext;
import com.blogspot.sontx.bottle.system.Resource;
import com.blogspot.sontx.bottle.view.custom.AutoSizeImageView;
import com.blogspot.sontx.bottle.view.custom.RichVideoView;
import com.blogspot.sontx.bottle.view.fragment.ListRoomMessageFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import it.slyce.messaging.utils.DateTimeUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
            return new TextViewHolder(view);
        } else if (viewType == TYPE_PHOTO) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_room_message_photo, parent, false);
            return new PhotoViewHolder(view);
        } else if (viewType == TYPE_VIDEO) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_room_message_video, parent, false);
            return new VideoViewHolder(view);
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        RoomMessage roomMessage = values.get(position);
        PublicProfile owner = roomMessage.getOwner();

        // apply for all message types
        final TextViewHolder textViewHolder = (TextViewHolder) holder;

        textViewHolder.item = roomMessage;
        textViewHolder.displayNameView.setText(owner.getDisplayName());
        String url = resource.absoluteUrl(owner.getAvatarUrl());
        Picasso.with(textViewHolder.root.getContext()).load(url).into(textViewHolder.avatarView);
        textViewHolder.timestampView.setText(DateTimeUtils.getTimestamp(textViewHolder.root.getContext(), roomMessage.getTimestamp()));
        textViewHolder.textContentView.setText(roomMessage.getText());

        textViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onListRoomMessageInteraction((RoomMessage) textViewHolder.item);
                }
            }
        });
        textViewHolder.directMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener)
                    listener.onDirectMessageClick((RoomMessage) textViewHolder.item);
            }
        });
        textViewHolder.voteMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener)
                    listener.onVoteMessageClick((RoomMessage) textViewHolder.item);
            }
        });

        if (roomMessage.getOwner().getId().equals(currentUserId))
            textViewHolder.interactionView.setVisibility(View.GONE);
        else
            textViewHolder.interactionView.setVisibility(View.VISIBLE);

        // message type is photo
        if (holder.getItemViewType() == TYPE_PHOTO) {
            PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;
            url = resource.absoluteUrl(roomMessage.getMediaUrl());
            photoViewHolder.autoSizeImageView.setImageUrl(url);
        } else if (holder.getItemViewType() == TYPE_VIDEO) {
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
            url = resource.absoluteUrl(roomMessage.getMediaUrl());
            videoViewHolder.richVideoView.setVideoUrl(url);
        }
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    @Data
    public static class TextViewHolder extends RecyclerView.ViewHolder {
        final View root;
        final CircleImageView avatarView;
        final TextView displayNameView;
        final TextView timestampView;
        final TextView textContentView;
        final View directMessageView;
        final View voteMessageView;
        final View interactionView;
        final View moreOptionView;

        MessageBase item;

        public TextViewHolder(View view) {
            super(view);
            root = view;
            displayNameView = ButterKnife.findById(view, R.id.display_name_view);
            timestampView = ButterKnife.findById(view, R.id.timestamp_view);
            avatarView = ButterKnife.findById(view, R.id.avatar_view);
            textContentView = ButterKnife.findById(view, R.id.text_content_view);
            directMessageView = ButterKnife.findById(view, R.id.direct_message_view);
            voteMessageView = ButterKnife.findById(view, R.id.vote_message_view);
            interactionView = ButterKnife.findById(view, R.id.interaction_layout);
            moreOptionView = ButterKnife.findById(view, R.id.more_option_message_view);
        }
    }

    @EqualsAndHashCode(callSuper = false)
    @Data
    public static class PhotoViewHolder extends TextViewHolder {
        final AutoSizeImageView autoSizeImageView;

        public PhotoViewHolder(View view) {
            super(view);
            autoSizeImageView = ButterKnife.findById(view, R.id.image_view);
        }
    }

    public static class VideoViewHolder extends TextViewHolder {
        final RichVideoView richVideoView;

        VideoViewHolder(View view) {
            super(view);
            richVideoView = ButterKnife.findById(view, R.id.video_view);
        }
    }
}
