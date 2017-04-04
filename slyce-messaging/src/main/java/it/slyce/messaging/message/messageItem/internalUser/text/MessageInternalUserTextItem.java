package it.slyce.messaging.message.messageItem.internalUser.text;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import it.slyce.messaging.message.TextMessage;
import it.slyce.messaging.message.messageItem.MessageViewHolder;
import it.slyce.messaging.message.messageItem.master.text.MessageTextItem;
import it.slyce.messaging.utils.StringUtils;

/**
 * Created by John C. Hunchar on 5/12/16.
 */
public class MessageInternalUserTextItem extends MessageTextItem {

    public MessageInternalUserTextItem(TextMessage messageText, Context context) {
        super(messageText, context);
    }

    @Override
    public void buildMessageItem(MessageViewHolder messageViewHolder) {
        super.buildMessageItem(messageViewHolder);
        if (message != null && messageViewHolder != null) {
            String state = StringUtils.toUppercaseFirstCharacter(message.getState());

            TextView stateView = ((MessageInternalUserTextViewHolder) messageViewHolder).state;

            stateView.setText(state);
            stateView.setVisibility(isLastConsecutiveMessageFromSource ? View.VISIBLE : View.GONE);
        }
    }
}
