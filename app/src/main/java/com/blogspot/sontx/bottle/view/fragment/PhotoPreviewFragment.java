package com.blogspot.sontx.bottle.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blogspot.sontx.bottle.R;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;

public class PhotoPreviewFragment extends PreviewFragmentBase {
    private static final String PHOTO_PATH = "photo-path";

    @BindView(R.id.photo_preview)
    ImageView previewImage;
    private String photoPath;

    public PhotoPreviewFragment() {
    }

    public static PhotoPreviewFragment newInstance(String photoPath) {
        PhotoPreviewFragment photoPreviewFragment = new PhotoPreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PHOTO_PATH, photoPath);
        photoPreviewFragment.setArguments(bundle);
        return photoPreviewFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            photoPath = getArguments().getString(PHOTO_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_preview, container, false);
        setupView(view);
        if (photoPath != null)
            Picasso.with(view.getContext()).load(new File(photoPath)).into(previewImage);
        return view;
    }
}
