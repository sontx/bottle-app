package com.blogspot.sontx.bottle.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blogspot.sontx.bottle.Constants;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelDetail;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelMember;
import com.blogspot.sontx.bottle.utils.DateTimeUtils;
import com.blogspot.sontx.bottle.view.fragment.ChannelFragment.OnChannelInteractionListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Getter;
import lombok.Setter;

public class ChannelRecyclerViewAdapter extends RecyclerView.Adapter<ChannelRecyclerViewAdapter.ViewHolder> {

    @Getter
    @Setter
    private List<Channel> values;
    @Setter
    private String currentUserId;
    @Setter
    private OnChannelInteractionListener listener;

    public ChannelRecyclerViewAdapter(List<Channel> items) {
        values = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_channel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Channel channel = values.get(position);
        holder.item = channel;

        PublicProfile anotherGuy = getAnotherGuy(channel);
        ChannelDetail detail = channel.getDetail();

        holder.titleView.setText(anotherGuy != null ? anotherGuy.getDisplayName() : "Unknown");
        holder.subtitleView.setText(detail != null ? DateTimeUtils.getTimestamp(detail.getTimestamp()) : "...");

        String avatarUrl = anotherGuy != null ? anotherGuy.getAvatarUrl() : System.getProperty(Constants.UI_DEFAULT_AVATAR_URL_KEY);
        Picasso.with(holder.root.getContext()).load(avatarUrl).into(holder.avatarView);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener)
                    listener.onChannelInteraction(holder.item);
            }
        });
    }

    private PublicProfile getAnotherGuy(Channel channel) {
        List<ChannelMember> memberList = channel.getMemberList();
        if (memberList == null || memberList.size() < 2)
            return null;
        if (memberList.get(0).getId().equals(currentUserId))
            return memberList.get(1).getPublicProfile();
        return memberList.get(0).getPublicProfile();
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View root;
        final CircleImageView avatarView;
        final TextView titleView;
        final TextView subtitleView;
        Channel item;

        ViewHolder(View view) {
            super(view);
            root = view;
            titleView = ButterKnife.findById(view, R.id.title_view);
            subtitleView = ButterKnife.findById(view, R.id.detail_view);
            avatarView = ButterKnife.findById(view, R.id.avatar_view);
        }
    }
}
