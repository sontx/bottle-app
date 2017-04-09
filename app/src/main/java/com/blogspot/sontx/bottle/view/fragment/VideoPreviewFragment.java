package com.blogspot.sontx.bottle.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.blogspot.sontx.bottle.R;

public class VideoPreviewFragment extends PreviewFragmentBase {
    //@BindView(R.id.video_preview)
    VideoView previewImage;

    private String videoPath;

    public VideoPreviewFragment() {
    }

    public static VideoPreviewFragment newInstance() {
        return new VideoPreviewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_photo_preview, container, false);
        setupView(view);
        previewImage.setVideoPath(videoPath);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (videoPath != null)
            previewImage.setVideoPath(videoPath);
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
        if (previewImage != null)
            previewImage.setVideoPath(videoPath);
    }
}
