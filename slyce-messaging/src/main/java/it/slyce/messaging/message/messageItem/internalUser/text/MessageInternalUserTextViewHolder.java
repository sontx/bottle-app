package it.slyce.messaging.message.messageItem.internalUser.text;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.slyce.messaging.R;
import it.slyce.messaging.message.messageItem.master.text.MessageTextViewHolder;
import it.slyce.messaging.utils.CustomSettings;

/**
 * Created by John C. Hunchar on 5/12/16.
 */
public class MessageInternalUserTextViewHolder extends MessageTextViewHolder {
    public TextView state;

    public MessageInternalUserTextViewHolder(View itemView, CustomSettings customSettings) {
        super(itemView, customSettings);

        avatar = (ImageView) itemView.findViewById(R.id.message_user_text_image_view_avatar);
        carrot = (ImageView) itemView.findViewById(R.id.message_user_text_image_view_carrot);
        initials = (TextView) itemView.findViewById(R.id.message_user_text_text_view_initials);
        text = (TextView) itemView.findViewById(R.id.message_user_text_text_view_text);
        state = (TextView) itemView.findViewById(R.id.message_user_text_text_view_state);
        timestamp = (TextView) itemView.findViewById(R.id.message_user_text_text_view_timestamp);
        avatarContainer = (ViewGroup) itemView.findViewById(R.id.message_user_text_view_group_avatar);
        bubble = (ViewGroup) itemView.findViewById(R.id.message_user_text_view_group_bubble);

        Drawable drawable = ContextCompat.getDrawable(itemView.getContext(), R.drawable.shape_rounded_rectangle_white);
        // Drawable drawable = itemView.getContext().getDrawable();
        drawable.setColorFilter(customSettings.localBubbleBackgroundColor, PorterDuff.Mode.SRC_ATOP);
        bubble.setBackground(drawable);
        carrot.setColorFilter(customSettings.localBubbleBackgroundColor);
        text.setTextColor(customSettings.localBubbleTextColor);
        state.setTextColor(customSettings.stateColor);
        timestamp.setTextColor(customSettings.timestampColor);
    }
}
