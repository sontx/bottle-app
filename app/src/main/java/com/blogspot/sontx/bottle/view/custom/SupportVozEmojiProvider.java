package com.blogspot.sontx.bottle.view.custom;

import android.support.annotation.NonNull;

import com.blogspot.sontx.bottle.R;
import com.vanniktech.emoji.EmojiProvider;
import com.vanniktech.emoji.emoji.Emoji;
import com.vanniktech.emoji.emoji.EmojiCategory;
import com.vanniktech.emoji.one.category.ActivityCategory;
import com.vanniktech.emoji.one.category.FoodsCategory;
import com.vanniktech.emoji.one.category.NatureCategory;
import com.vanniktech.emoji.one.category.ObjectsCategory;
import com.vanniktech.emoji.one.category.PeopleCategory;
import com.vanniktech.emoji.one.category.PlacesCategory;
import com.vanniktech.emoji.one.category.SymbolsCategory;

public class SupportVozEmojiProvider implements EmojiProvider {
    @NonNull
    @Override
    public EmojiCategory[] getCategories() {
        return new EmojiCategory[]{
                new VozCategory(),
                new PeopleCategory(),
                new NatureCategory(),
                new FoodsCategory(),
                new ActivityCategory(),
                new PlacesCategory(),
                new ObjectsCategory(),
                new SymbolsCategory()
        };
    }

    private static class VozCategory implements EmojiCategory {

        private int[] codepoints(String st) {
            int[] codepoints = new int[st.length()];
            for (int i = 0; i < codepoints.length; i++) {
                codepoints[i] = st.codePointAt(i);
            }
            return codepoints;
        }

        @NonNull
        @Override
        public Emoji[] getEmojis() {
            return new Emoji[]{
                    new Emoji(codepoints(":adore:"), R.drawable.emoji_voz_adore),
                    new Emoji(codepoints(":after-boom:"), R.drawable.emoji_voz_after_boom),
                    new Emoji(codepoints(":ah:"), R.drawable.emoji_voz_ah),
                    new Emoji(codepoints(":amazed:"), R.drawable.emoji_voz_amazed),
                    new Emoji(codepoints(":angry:"), R.drawable.emoji_voz_angry),
                    new Emoji(codepoints(":bad-smelly:"), R.drawable.emoji_voz_bad_smelly),
                    new Emoji(codepoints(":baffle:"), R.drawable.emoji_voz_baffle),
                    new Emoji(codepoints(":beat-brick:"), R.drawable.emoji_voz_beat_brick),
                    new Emoji(codepoints(":beat-plaster:"), R.drawable.emoji_voz_beat_plaster),
                    new Emoji(codepoints(":beat-shot:"), R.drawable.emoji_voz_beat_shot),
                    new Emoji(codepoints(":beated:"), R.drawable.emoji_voz_beated),
                    new Emoji(codepoints(":beauty:"), R.drawable.emoji_voz_beauty),
                    new Emoji(codepoints(":big-smile:"), R.drawable.emoji_voz_big_smile),
                    new Emoji(codepoints(":boss:"), R.drawable.emoji_voz_boss),
                    new Emoji(codepoints(":burn-joss-stick:"), R.drawable.emoji_voz_burn_joss_stick),
                    new Emoji(codepoints(":byebye:"), R.drawable.emoji_voz_byebye),
                    new Emoji(codepoints(":canny:"), R.drawable.emoji_voz_canny),
                    new Emoji(codepoints(":choler:"), R.drawable.emoji_voz_choler),
                    new Emoji(codepoints(":cold:"), R.drawable.emoji_voz_cold),
                    new Emoji(codepoints(":confident:"), R.drawable.emoji_voz_confident),
                    new Emoji(codepoints(":confuse:"), R.drawable.emoji_voz_confuse),
                    new Emoji(codepoints(":cool:"), R.drawable.emoji_voz_cool),
                    new Emoji(codepoints(":cry:"), R.drawable.emoji_voz_cry),
                    new Emoji(codepoints(":doubt:"), R.drawable.emoji_voz_doubt),
                    new Emoji(codepoints(":dribble:"), R.drawable.emoji_voz_dribble),
                    new Emoji(codepoints(":embarrassed:"), R.drawable.emoji_voz_embarrassed),
                    new Emoji(codepoints(":extreme-sexy-girl:"), R.drawable.emoji_voz_extreme_sexy_girl),
                    new Emoji(codepoints(":feel-good:"), R.drawable.emoji_voz_feel_good),
                    new Emoji(codepoints(":go:"), R.drawable.emoji_voz_go),
                    new Emoji(codepoints(":haha:"), R.drawable.emoji_voz_haha),
                    new Emoji(codepoints(":hell-boy:"), R.drawable.emoji_voz_hell_boy),
                    new Emoji(codepoints(":hungry:"), R.drawable.emoji_voz_hungry),
                    new Emoji(codepoints(":look-down:"), R.drawable.emoji_voz_look_down),
                    new Emoji(codepoints(":matrix:"), R.drawable.emoji_voz_matrix),
                    new Emoji(codepoints(":misdoubt:"), R.drawable.emoji_voz_misdoubt),
                    new Emoji(codepoints(":nosebleed:"), R.drawable.emoji_voz_nosebleed),
                    new Emoji(codepoints(":oh:"), R.drawable.emoji_voz_oh),
                    new Emoji(codepoints(":ops:"), R.drawable.emoji_voz_ops),
                    new Emoji(codepoints(":pudency:"), R.drawable.emoji_voz_pudency),
                    new Emoji(codepoints(":rap:"), R.drawable.emoji_voz_rap),
                    new Emoji(codepoints(":sad:"), R.drawable.emoji_voz_sad),
                    new Emoji(codepoints(":sexy-girl:"), R.drawable.emoji_voz_sexy_girl),
                    new Emoji(codepoints(":shame:"), R.drawable.emoji_voz_shame),
                    new Emoji(codepoints(":smile:"), R.drawable.emoji_voz_smile),
                    new Emoji(codepoints(":spiderman:"), R.drawable.emoji_voz_spiderman),
                    new Emoji(codepoints(":still-dreaming:"), R.drawable.emoji_voz_still_dreaming),
                    new Emoji(codepoints(":sure:"), R.drawable.emoji_voz_sure),
                    new Emoji(codepoints(":surrender:"), R.drawable.emoji_voz_surrender),
                    new Emoji(codepoints(":sweat:"), R.drawable.emoji_voz_sweat),
                    new Emoji(codepoints(":sweet-kiss:"), R.drawable.emoji_voz_sweet_kiss),
                    new Emoji(codepoints(":tire:"), R.drawable.emoji_voz_tire),
                    new Emoji(codepoints(":too-sad:"), R.drawable.emoji_voz_too_sad),
                    new Emoji(codepoints(":waaaht:"), R.drawable.emoji_voz_waaaht),
                    new Emoji(codepoints(":what:"), R.drawable.emoji_voz_what)
            };
        }

        @Override
        public int getIcon() {
            return R.drawable.ic_banana;
        }
    }
}
