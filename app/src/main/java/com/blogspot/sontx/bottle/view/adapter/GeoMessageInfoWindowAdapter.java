package com.blogspot.sontx.bottle.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.GeoMessage;
import com.blogspot.sontx.bottle.model.bean.MessageBase;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.system.BottleContext;
import com.blogspot.sontx.bottle.system.Resource;
import com.blogspot.sontx.bottle.view.custom.AutoSizeImageView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import butterknife.ButterKnife;
import it.slyce.messaging.utils.DateTimeUtils;

public class GeoMessageInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final Context context;
    private final Resource resource;
    private TextViewHolder textViewHolder;
    private PhotoViewHolder photoViewHolder;

    public GeoMessageInfoWindowAdapter(Context context) {
        this.context = context;
        BottleContext bottleContext = App.getInstance().getBottleContext();
        resource = bottleContext.getResource();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return render(marker);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private View render(Marker marker) {
        Object tag = marker.getTag();
        if (tag instanceof GeoMessage) {
            GeoMessage geoMessage = (GeoMessage) tag;
            if (MessageBase.PHOTO.equalsIgnoreCase(geoMessage.getType()))
                return renderPhotoView(geoMessage, marker);
            return renderTextView(geoMessage);
        }
        return null;
    }

    private View renderTextView(GeoMessage geoMessage) {
        if (textViewHolder == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fragment_geo_message_text, null);
            textViewHolder = new TextViewHolder(view);
        }

        PublicProfile owner = geoMessage.getOwner();

        textViewHolder.message = geoMessage;
        textViewHolder.displayNameView.setText(owner.getDisplayName());
        textViewHolder.timestampView.setText(DateTimeUtils.getTimestamp(textViewHolder.root.getContext(), geoMessage.getTimestamp()));
        textViewHolder.contentView.setText(geoMessage.getText());

        return textViewHolder.root;
    }

    private View renderPhotoView(GeoMessage geoMessage, Marker marker) {
        if (photoViewHolder == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fragment_geo_message_photo, null);
            photoViewHolder = new PhotoViewHolder(view);
        }

        PublicProfile owner = geoMessage.getOwner();

        photoViewHolder.message = geoMessage;
        photoViewHolder.displayNameView.setText(owner.getDisplayName());
        photoViewHolder.contentView.setText(geoMessage.getText());

        String url = resource.absoluteUrl(geoMessage.getMediaUrl());
        if (!photoViewHolder.imageView.isLoaded() || !url.equals(photoViewHolder.imageView.getCurrentImageUrl()))
            photoViewHolder.imageView.fixImage(url, marker);

        return photoViewHolder.root;
    }

    private static abstract class ViewHolder {
        final View root;
        GeoMessage message;

        ViewHolder(View root) {
            this.root = root;
        }
    }

    private static class TextViewHolder extends ViewHolder {
        final TextView contentView;
        final TextView displayNameView;
        final TextView timestampView;

        private TextViewHolder(View root) {
            super(root);
            contentView = ButterKnife.findById(root, R.id.content_view);
            displayNameView = ButterKnife.findById(root, R.id.display_name_view);
            timestampView = ButterKnife.findById(root, R.id.timestamp_view);
        }
    }

    private static class PhotoViewHolder extends ViewHolder {
        final TextView contentView;
        final TextView displayNameView;
        final AutoSizeImageView imageView;

        private PhotoViewHolder(View root) {
            super(root);
            contentView = ButterKnife.findById(root, R.id.content_view);
            displayNameView = ButterKnife.findById(root, R.id.display_name_view);
            imageView = ButterKnife.findById(root, R.id.image_view);
        }
    }
}
