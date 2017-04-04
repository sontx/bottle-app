package it.slyce.messaging.message.messageItem.master.text;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.slyce.messaging.message.messageItem.MessageViewHolder;
import it.slyce.messaging.utils.CustomSettings;

/**
 * Created by matthewpage on 6/27/16.
 */
public abstract class MessageTextViewHolder extends MessageViewHolder {
    public ImageView carrot;
    public TextView text;
    public ViewGroup bubble;

    public MessageTextViewHolder(View itemView, CustomSettings customSettings) {
        super(itemView, customSettings);
    }
}