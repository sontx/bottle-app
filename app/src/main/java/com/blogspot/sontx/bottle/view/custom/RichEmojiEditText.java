package com.blogspot.sontx.bottle.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import lombok.Setter;

public class RichEmojiEditText extends com.vanniktech.emoji.EmojiEditText {
    @Setter
    private OnBackPressedListener onBackPressedListener;

    public RichEmojiEditText(Context context) {
        super(context);
    }

    public RichEmojiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (onBackPressedListener != null && keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (onBackPressedListener.onBackPressed(this))
                return true;
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
