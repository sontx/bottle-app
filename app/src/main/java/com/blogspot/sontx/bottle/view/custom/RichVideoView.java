package com.blogspot.sontx.bottle.view.custom;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.VideoView;

public class RichVideoView extends VideoView {
    public RichVideoView(Context context) {
        super(context);
    }

    public RichVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RichVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RichVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setVideoUrl(String url) {

    }
}
