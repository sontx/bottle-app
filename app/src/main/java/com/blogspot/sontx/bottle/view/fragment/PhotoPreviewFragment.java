package com.blogspot.sontx.bottle.view.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blogspot.sontx.bottle.R;

import butterknife.BindView;

public class PhotoPreviewFragment extends PreviewFragmentBase {
    @BindView(R.id.photo_preview)
    ImageView previewImage;

    private Bitmap bitmap;

    public PhotoPreviewFragment() {
    }

    public static PhotoPreviewFragment newInstance() {
        return new PhotoPreviewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_photo_preview, container, false);
        setupView(view);
        previewImage.setImageBitmap(bitmap);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (bitmap != null)
            previewImage.setImageBitmap(bitmap);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        if (previewImage != null)
            previewImage.setImageBitmap(bitmap);
    }
}
