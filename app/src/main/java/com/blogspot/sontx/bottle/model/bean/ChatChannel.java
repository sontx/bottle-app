package com.blogspot.sontx.bottle.model.bean;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;

@Data
public class ChatChannel {
    private String id;
    private String recipient;
    private String lastInteractTime;
    private String recipientAvatarUrl;

    public static List<ChatChannel> getDummyList() {
        ChatChannel chatChannel1 = new ChatChannel();
        chatChannel1.setId("0");
        chatChannel1.setRecipient("sontx1");
        chatChannel1.setLastInteractTime("1 minute ago");
        chatChannel1.setRecipientAvatarUrl("https://scontent.fdad3-1.fna.fbcdn.net/v/t1.0-9/16939522_1090297181116017_187085660336039003_n.jpg?oh=dbd78feb7019fe697a641a61e7badfde&oe=596D3FC5");

        ChatChannel chatChannel2 = new ChatChannel();
        chatChannel2.setId("1");
        chatChannel2.setRecipient("sontx2");
        chatChannel2.setLastInteractTime("now");
        chatChannel2.setRecipientAvatarUrl("https://scontent.fdad3-1.fna.fbcdn.net/v/t1.0-9/12592596_1555320444798787_8779189816870510616_n.jpg?oh=a3537c689cf1686a93e825958623ba09&oe=596DEC62");

        ChatChannel chatChannel3 = new ChatChannel();
        chatChannel3.setId("2");
        chatChannel3.setRecipient("sontx3");
        chatChannel3.setLastInteractTime("yesterday");
        chatChannel3.setRecipientAvatarUrl("https://scontent.fdad3-1.fna.fbcdn.net/v/t1.0-9/16603014_1027314210735357_4572730096735224657_n.jpg?oh=fc8401407809fe5143f4b6bf7dc5efba&oe=59616E7E");

        List<ChatChannel> chatChannels = new LinkedList<>();
        chatChannels.add(chatChannel1);
        chatChannels.add(chatChannel2);
        chatChannels.add(chatChannel3);

        return chatChannels;
    }
}
