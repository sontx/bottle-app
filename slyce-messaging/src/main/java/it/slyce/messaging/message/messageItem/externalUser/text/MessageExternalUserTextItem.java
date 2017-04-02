package it.slyce.messaging.message.messageItem.externalUser.text;

import android.content.Context;

import it.slyce.messaging.message.TextMessage;
import it.slyce.messaging.message.messageItem.master.text.MessageTextItem;
import it.slyce.messaging.utils.DateUtils;

/**
 * Created by John C. Hunchar on 5/12/16.
 */
public class MessageExternalUserTextItem extends MessageTextItem {

    public MessageExternalUserTextItem(TextMessage messageText, Context context) {
        super(messageText, context);
    }

    @Override
    protected String getSubtitle() {
        return DateUtils.getTimestamp(context, message.getDate());
    }
}
