package com.blogspot.sontx.bottle.view.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.GeoMessage;
import com.blogspot.sontx.bottle.model.bean.MessageBase;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.system.BottleContext;
import com.blogspot.sontx.bottle.system.Resource;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;

import it.slyce.messaging.utils.DateTimeUtils;

import static com.blogspot.sontx.bottle.view.adapter.RoomMessageRecyclerViewAdapter.PhotoViewHolder;
import static com.blogspot.sontx.bottle.view.adapter.RoomMessageRecyclerViewAdapter.TextViewHolder;

public class GeoMessageDialog implements OnClickListener {
    private final OnGeoMessageDialogInteractionListener listener;
    private TextViewHolder textViewHolder = null;

    public GeoMessageDialog(Context context, GeoMessage geoMessage, OnGeoMessageDialogInteractionListener listener) {
        this.listener = listener;
        Holder holder;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (geoMessage.getType().equalsIgnoreCase(MessageBase.TEXT))
            textViewHolder = new TextViewHolder(inflater.inflate(R.layout.fragment_room_message_text, null));
        else if (geoMessage.getType().equalsIgnoreCase(MessageBase.PHOTO))
            textViewHolder = new PhotoViewHolder(inflater.inflate(R.layout.fragment_room_message_photo, null));

        BottleContext bottleContext = App.getInstance().getBottleContext();
        Resource resource = bottleContext.getResource();

        PublicProfile owner = geoMessage.getOwner();

        textViewHolder.setItem(geoMessage);
        textViewHolder.getDisplayNameView().setText(owner.getDisplayName());
        String url = resource.absoluteUrl(owner.getAvatarUrl());
        Picasso.with(textViewHolder.getRoot().getContext()).load(url).into(textViewHolder.getAvatarView());
        textViewHolder.getTimestampView().setText(DateTimeUtils.getTimestamp(textViewHolder.getRoot().getContext(), geoMessage.getTimestamp()));
        textViewHolder.getTextContentView().setText(geoMessage.getText());

        String currentUserId = bottleContext.getCurrentBottleUser().getUid();
        if (geoMessage.getOwner().getId().equals(currentUserId))
            textViewHolder.getInteractionView().setVisibility(View.GONE);
        else
            textViewHolder.getInteractionView().setVisibility(View.VISIBLE);

        if (textViewHolder instanceof PhotoViewHolder) {
            PhotoViewHolder photoViewHolder = (PhotoViewHolder) textViewHolder;
            url = resource.absoluteUrl(geoMessage.getMediaUrl());
            photoViewHolder.getAutoSizeImageView().setImageUrl(url);
        }

        holder = new ViewHolder(textViewHolder.itemView);

        DialogPlus dialogPlus = DialogPlus.newDialog(context)
                .setContentHolder(holder)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .setOnClickListener(this)
                .create();
        dialogPlus.show();
    }

    @Override
    public void onClick(DialogPlus dialog, View view) {
        if (view.getId() == R.id.direct_message_view) {
            if (listener != null)
                listener.onDirectMessageClick((GeoMessage) textViewHolder.getItem());
            dialog.dismiss();
        }
    }

    public interface OnGeoMessageDialogInteractionListener {
        void onDirectMessageClick(GeoMessage geoMessage);
    }
}
