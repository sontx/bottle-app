package it.slyce.messaging.message.messageItem.externalUser.media;

import android.content.Context;

import it.slyce.messaging.message.MediaMessage;
import it.slyce.messaging.message.messageItem.master.media.MessageMediaItem;
import it.slyce.messaging.utils.DateUtils;

/**
 * Created by John C. Hunchar on 5/12/16.
 */
public class MessageExternalUserMediaItem extends MessageMediaItem {

    public MessageExternalUserMediaItem(MediaMessage messageMedia, Context context) {
        super(messageMedia, context);
    }

    @Override
    protected String getSubtitle() {
        return DateUtils.getTimestamp(context, message.getDate());
    }
}
