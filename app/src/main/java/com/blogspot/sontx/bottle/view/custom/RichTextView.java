package com.blogspot.sontx.bottle.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.blogspot.sontx.bottle.R;

public class RichTextView extends AppCompatTextView {

    private void setTint(int tint, Drawable drawableLeft) {
        drawableLeft.setColorFilter(tint, PorterDuff.Mode.MULTIPLY);
        setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
        invalidate();
    }

    public RichTextView(Context context) {
        super(context);
    }

    public RichTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupAttributes(attrs);
    }

    public RichTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupAttributes(attrs);
    }

    public void setupAttributes(AttributeSet attrs) {
        TypedArray array = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.RichTextView, 0, 0);
        try {
            Drawable drawableLeft = getCompoundDrawables()[0];
            setTint(array.getColor(R.styleable.RichTextView_tint, Color.BLACK), drawableLeft);
        } finally {
            array.recycle();
        }
    }
}
