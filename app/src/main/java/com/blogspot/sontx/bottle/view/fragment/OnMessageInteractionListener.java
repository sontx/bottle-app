package com.blogspot.sontx.bottle.view.fragment;

import com.blogspot.sontx.bottle.model.bean.MessageBase;

public interface OnMessageInteractionListener {
    void onDirectMessageClick(MessageBase item);

    void onVoteMessageClick(MessageBase item);

    void onUpdateMessageClick(MessageBase messageBase);

    void onDeleteMessageClick(MessageBase messageBase);
}
