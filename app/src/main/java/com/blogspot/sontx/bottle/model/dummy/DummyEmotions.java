package com.blogspot.sontx.bottle.model.dummy;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.Emotion;

import java.util.ArrayList;
import java.util.List;

public final class DummyEmotions {
    private static List<Emotion> emotions = null;
    private static int[] currentUserEmotionResIds = {
            R.drawable.me_banana,
            R.drawable.me_vegetable,
            R.drawable.me_love,
            R.drawable.me_haha,
            R.drawable.me_wow,
            R.drawable.me_sad,
            R.drawable.me_angry
    };

    public static List<Emotion> getEmotions() {
        if (emotions == null) {
            emotions = new ArrayList<>(7);
            emotions.add(new Emotion(R.drawable.banana, "Banana"));
            emotions.add(new Emotion(R.drawable.vegetable, "Vegetable"));
            emotions.add(new Emotion(R.drawable.love, "Love"));
            emotions.add(new Emotion(R.drawable.haha, "Haha"));
            emotions.add(new Emotion(R.drawable.wow, "Wow"));
            emotions.add(new Emotion(R.drawable.sad, "Sad"));
            emotions.add(new Emotion(R.drawable.angry, "Angry"));
        }
        return emotions;
    }

    public static int getEmotionResId(int emotion, boolean isCurrentUser) {
        if (emotion < 0 || emotion >= currentUserEmotionResIds.length)
            emotion = 0;
        if (isCurrentUser)
            return currentUserEmotionResIds[emotion];
        return getEmotions().get(emotion).getImageResId();
    }

    private DummyEmotions() {
    }
}
