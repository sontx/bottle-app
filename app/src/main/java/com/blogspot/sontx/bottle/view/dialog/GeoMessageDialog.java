package com.blogspot.sontx.bottle.view.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.GeoMessage;
import com.blogspot.sontx.bottle.model.bean.MessageBase;
import com.blogspot.sontx.bottle.system.BottleContext;
import com.blogspot.sontx.bottle.system.Resource;
import com.blogspot.sontx.bottle.view.adapter.MessageAdapterHelper;
import com.blogspot.sontx.bottle.view.fragment.OnMessageInteractionListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;

public class GeoMessageDialog implements OnClickListener {
    private final OnMessageInteractionListener listener;
    private final String currentUserId;
    private MessageAdapterHelper.TextViewHolder textViewHolder = null;

    public GeoMessageDialog(Context context, GeoMessage geoMessage) {
        this.listener = (OnMessageInteractionListener) context;
        Holder holder;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (geoMessage.getType().equalsIgnoreCase(MessageBase.TEXT))
            textViewHolder = new MessageAdapterHelper.TextViewHolder(inflater.inflate(R.layout.fragment_room_message_text, null));
        else if (geoMessage.getType().equalsIgnoreCase(MessageBase.PHOTO))
            textViewHolder = new MessageAdapterHelper.PhotoViewHolder(inflater.inflate(R.layout.fragment_room_message_photo, null));

        BottleContext bottleContext = App.getInstance().getBottleContext();
        Resource resource = bottleContext.getResource();
        currentUserId = bottleContext.getCurrentBottleUser().getUid();

        MessageAdapterHelper.bindMessageToTextViewHolder(geoMessage, textViewHolder, resource, currentUserId, listener);

        if (textViewHolder instanceof MessageAdapterHelper.PhotoViewHolder)
            MessageAdapterHelper.bindMessageToPhotoViewHolder((MessageAdapterHelper.PhotoViewHolder) textViewHolder, geoMessage, resource);

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
        if (view.getId() == textViewHolder.directMessageView.getId()) {
            MessageAdapterHelper.fireOnDirectMessageClick(listener, textViewHolder);
            dialog.dismiss();
        } else if (view.getId() == textViewHolder.voteMessageView.getId()) {
            MessageAdapterHelper.fireOnVoteMessageClick(listener, textViewHolder);
        } else if (view.getId() == textViewHolder.moreOptionView.getId()) {
            MessageAdapterHelper.fireOnEditMessageClick(textViewHolder, currentUserId, listener);
        } else if (view.getId() == textViewHolder.ignoreEditView.getId()) {
            MessageAdapterHelper.fireOnIgnoreChangeClick(textViewHolder, currentUserId);
        } else if (view.getId() == textViewHolder.applyEditView.getId()) {
            MessageAdapterHelper.fireOnApplyChangeClick(textViewHolder, currentUserId, listener);
        }
    }
}
