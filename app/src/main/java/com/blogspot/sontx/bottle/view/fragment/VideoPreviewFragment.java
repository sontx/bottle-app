package com.blogspot.sontx.bottle.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.blogspot.sontx.bottle.R;

import butterknife.BindView;

public class VideoPreviewFragment extends PreviewFragmentBase {
    @BindView(R.id.video_preview)
    VideoView previewVideoView;
    private MediaController mediaController;

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
        View view = inflater.inflate(R.layout.fragment_video_preview, container, false);
        setupView(view);
        mediaController = new MediaController(getContext());
        previewVideoView.setMediaController(mediaController);
        if (videoPath != null)
            showVideo();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (videoPath != null)
            previewVideoView.setVideoPath(videoPath);
    }

    private void showVideo() {
        previewVideoView.setVideoPath(videoPath);
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
        if (previewVideoView != null)
            showVideo();
    }
}
