package com.blogspot.sontx.bottle.view.custom;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.MediaController;
import android.widget.VideoView;

public class RichVideoView extends VideoView {
    private MediaController mediaController;

    public RichVideoView(Context context) {
        super(context);
        setup();
    }

    public RichVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public RichVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RichVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup();
    }

    private void setup() {
        mediaController = new MediaController(getContext());
        setMediaController(mediaController);
    }

    public void setVideoUrl(String url) {
        if (url.startsWith("file://")) {
            String videoPath = url.substring("file://".length());
            setVideoPath(videoPath);
        } else {
            setVideoURI(Uri.parse(url));
        }
    }
}
