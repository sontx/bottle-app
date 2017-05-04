package it.slyce.messaging.listeners;

import android.net.Uri;
import android.view.View;

/**
 * Created by matthewpage on 6/21/16.
 */
public interface UserSendsMessageListener {
    void onUserSendsTextMessage(String text, int internalId);

    void onUserSendsMediaMessage(Uri imageUri, int internalId);

    void onShowEmojiKeyboard(View view);
}
