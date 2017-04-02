package it.slyce.messaging.message;

import android.content.Context;

import it.slyce.messaging.message.messageItem.MessageItem;

/**
 * Created by matthewpage on 6/21/16.
 */
public abstract class Message {
    private static int freeId = 0;

    long date;
    MessageSource source;
    String avatarUrl;
    String displayName;
    String userId;
    String initials;
    String state;
    String id;
    int tempId;

    public Message() {
        tempId = ++freeId;
    }

    public int getTempId() {
        return tempId;
    }

    public void setTempId(int tempId) {
        this.tempId = tempId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public MessageSource getSource() {
        return source;
    }

    public void setSource(MessageSource source) {
        this.source = source;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public abstract MessageItem toMessageItem(Context context);
}
