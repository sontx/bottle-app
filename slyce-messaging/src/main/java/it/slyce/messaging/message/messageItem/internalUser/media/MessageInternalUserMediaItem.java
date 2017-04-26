package it.slyce.messaging.message.messageItem.internalUser.media;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import it.slyce.messaging.message.MediaMessage;
import it.slyce.messaging.message.messageItem.MessageViewHolder;
import it.slyce.messaging.message.messageItem.master.media.MessageMediaItem;
import it.slyce.messaging.utils.StringUtils;

/**
 * Created by John C. Hunchar on 5/12/16.
 */
public class MessageInternalUserMediaItem extends MessageMediaItem {

    public MessageInternalUserMediaItem(MediaMessage messageMedia, Context context) {
        super(messageMedia, context);
    }

    @Override
    public void buildMessageItem(MessageViewHolder messageViewHolder) {
        super.buildMessageItem(messageViewHolder);
        if (message != null && messageViewHolder != null && messageViewHolder instanceof MessageInternalUserMediaViewHolder) {
            String state = StringUtils.toUppercaseFirstCharacter(message.getState());

            TextView stateView = ((MessageInternalUserMediaViewHolder) messageViewHolder).state;

            stateView.setText(state);
            stateView.setVisibility(isLastConsecutiveMessageFromSource ? View.VISIBLE : View.GONE);
        }
    }
}
