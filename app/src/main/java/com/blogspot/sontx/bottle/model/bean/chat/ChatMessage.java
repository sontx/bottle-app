package com.blogspot.sontx.bottle.model.bean.chat;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.io.Serializable;

import lombok.Data;

@Data
public class ChatMessage implements Serializable {
    public final static String TYPE_NONE = "none";
    public final static String TYPE_TEXT = "text";
    public final static String TYPE_MEDIA = "media";

    public final static String STATE_SENDING = "sending";
    public final static String STATE_SENT = "sent";
    public final static String STATE_RECEIVED = "received";
    public final static String STATE_SEEN = "seen";
    public final static String STATE_ERROR = "error";

    @Exclude
    private String channelId;
    @PropertyName("sender_id")
    private String senderId;
    private String message;
    @PropertyName("message_type")
    private String messageType;
    private String state;
    private long timestamp;
}
