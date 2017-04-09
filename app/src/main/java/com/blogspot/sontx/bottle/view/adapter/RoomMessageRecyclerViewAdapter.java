package com.blogspot.sontx.bottle.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.RoomMessage;
import com.blogspot.sontx.bottle.view.fragment.ListRoomMessageFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import it.slyce.messaging.utils.DateTimeUtils;
import lombok.Getter;

public class RoomMessageRecyclerViewAdapter extends RecyclerView.Adapter<RoomMessageRecyclerViewAdapter.ViewHolder> {

    private final ListRoomMessageFragment.OnListRoomMessageInteractionListener listener;
    @Getter
    private List<RoomMessage> values;

    public RoomMessageRecyclerViewAdapter(List<RoomMessage> items, ListRoomMessageFragment.OnListRoomMessageInteractionListener listener) {
        values = items;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_room_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        RoomMessage roomMessage = values.get(position);
        PublicProfile owner = roomMessage.getOwner();
        holder.item = roomMessage;

        holder.displayNameView.setText(owner.getDisplayName());
        Picasso.with(holder.root.getContext()).load(owner.getAvatarUrl()).into(holder.avatarView);
        holder.timestampView.setText(DateTimeUtils.getTimestamp(holder.root.getContext(), roomMessage.getTimestamp()));
        holder.textContentView.setText(roomMessage.getText());

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onListRoomMessageInteraction(holder.item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View root;
        final CircleImageView avatarView;
        final TextView displayNameView;
        final TextView timestampView;
        final TextView textContentView;

        RoomMessage item;

        ViewHolder(View view) {
            super(view);
            root = view;
            displayNameView = ButterKnife.findById(view, R.id.display_name_view);
            timestampView = ButterKnife.findById(view, R.id.timestamp_view);
            avatarView = ButterKnife.findById(view, R.id.avatar_view);
            textContentView = ButterKnife.findById(view, R.id.text_content_view);
        }
    }
}
