package com.blogspot.sontx.bottle.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.Constants;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.model.bean.chat.ChannelDetail;
import com.blogspot.sontx.bottle.system.Resource;
import com.blogspot.sontx.bottle.utils.DateTimeUtils;
import com.blogspot.sontx.bottle.view.fragment.ListChannelFragment.OnChannelInteractionListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Getter;
import lombok.Setter;

public class ChannelRecyclerViewAdapter extends RecyclerView.Adapter<ChannelRecyclerViewAdapter.ViewHolder> {
    public static final int ITEM_DELETE_CONVERSATION = 0;
    public static final int ITEM_GENERATE_QRCODE = 1;
    public static final int ITEM_SCAN_QRCODE = 2;

    @Getter
    @Setter
    private List<Channel> values;
    private OnChannelInteractionListener listener;
    private Resource resource;

    @Getter
    @Setter
    private Channel selectedChannel;

    public ChannelRecyclerViewAdapter(List<Channel> items, OnChannelInteractionListener listener) {
        this.values = items;
        this.listener = listener;
        resource = App.getInstance().getBottleContext().getResource();
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

        PublicProfile anotherGuy = channel.getAnotherGuy().getPublicProfile();
        ChannelDetail detail = channel.getDetail();

        holder.titleView.setText(anotherGuy != null ? anotherGuy.getDisplayName() : null);
        holder.subtitleView.setText(detail.getLastMessage());
        holder.timestampView.setText(DateTimeUtils.getTimestamp(detail.getTimestamp()));

        String avatarUrl = anotherGuy != null ? anotherGuy.getAvatarUrl() : System.getProperty(Constants.UI_DEFAULT_AVATAR_URL_KEY);
        String url = resource.absoluteUrl(avatarUrl);
        Picasso.with(holder.root.getContext()).load(url).into(holder.avatarView);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener)
                    listener.onListChannelInteraction(holder.item);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectedChannel = holder.item;
                return false;
            }
        });
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        holder.root.setOnClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        final View root;
        final CircleImageView avatarView;
        final TextView titleView;
        final TextView subtitleView;
        final TextView timestampView;
        final View onlineView;

        Channel item;

        ViewHolder(View view) {
            super(view);
            view.setOnCreateContextMenuListener(this);
            root = view;
            titleView = ButterKnife.findById(view, R.id.title_view);
            subtitleView = ButterKnife.findById(view, R.id.detail_view);
            timestampView = ButterKnife.findById(view, R.id.timestamp_view);
            avatarView = ButterKnife.findById(view, R.id.avatar_view);
            onlineView = ButterKnife.findById(view, R.id.online_view);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            Channel channel = item;
            if (channel != null) {
                menu.setHeaderTitle(item.getAnotherGuy().getPublicProfile().getDisplayName());
                menu.add(Menu.NONE, ITEM_DELETE_CONVERSATION, 0, "Delete conversation");
                menu.add(Menu.NONE, ITEM_GENERATE_QRCODE, 0, "Generate QR Code");
                menu.add(Menu.NONE, ITEM_SCAN_QRCODE, 0, "Scan QR Code");
            }
        }
    }
}
