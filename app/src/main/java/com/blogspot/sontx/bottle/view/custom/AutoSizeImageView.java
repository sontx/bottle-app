package com.blogspot.sontx.bottle.view.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.blogspot.sontx.bottle.R;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import lombok.Getter;

public class AutoSizeImageView extends AppCompatImageView {
    private Marker marker;
    @Getter
    private boolean loaded = false;

    public AutoSizeImageView(Context context) {
        super(context);
    }

    public AutoSizeImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void autoSize(final String url, final int width, int height) {
        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                int imageWidth = bitmap.getWidth();
                int imageHeight = bitmap.getHeight();

                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                if (imageWidth > imageHeight)
                    layoutParams.height = width * imageHeight / imageWidth;
                else
                    layoutParams.height = width;

                if (marker == null)
                    Picasso.with(getContext()).load(url).resize(width, layoutParams.height).centerCrop().into(AutoSizeImageView.this);
                else
                    Picasso.with(getContext()).load(url).resize(width, layoutParams.height).centerCrop().into(AutoSizeImageView.this, new Callback() {
                        @Override
                        public void onSuccess() {
                            marker.showInfoWindow();
                            marker = null;
                        }

                        @Override
                        public void onError() {

                        }
                    });
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        setTag(target);
        Picasso.with(getContext()).load(url).into(target);
    }

    public AutoSizeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageUrl(String url, Marker marker) {
        this.marker = marker;
        setImageUrl(url);
    }

    public void setImageUrl(final String url) {
        int width = getWidth();
        if (width > 0) {
            autoSize(url, width, 0);
        } else {
            ViewTreeObserver viewTreeObserver = getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        autoSize(url, getWidth(), getHeight());
                    }
                });
            }
        }
    }

    public void fixImage(final String url, Marker marker) {
        loaded = false;
        this.marker = marker;
        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                setImageBitmap(bitmap);
                loaded = true;
                AutoSizeImageView.this.marker.showInfoWindow();
                AutoSizeImageView.this.marker = null;
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        setTag(target);
        int width = getResources().getDimensionPixelSize(R.dimen.geo_message_width);
        int height = getResources().getDimensionPixelSize(R.dimen.geo_message_height);
        Picasso.with(getContext()).load(url).resize(width, height).centerCrop().into(target);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setTag(null);
    }
}
