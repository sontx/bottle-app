package com.blogspot.sontx.bottle.view.custom;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class FrameLayout169 extends FrameLayout {
    public FrameLayout169(Context context) {
        super(context);
    }

    public FrameLayout169(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FrameLayout169(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FrameLayout169(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = (int) (w * 9F/ 16F);
        setLayoutParams(layoutParams);
    }
}
