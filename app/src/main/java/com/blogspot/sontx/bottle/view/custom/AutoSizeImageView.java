package com.blogspot.sontx.bottle.view.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class AutoSizeImageView extends AppCompatImageView {
    public AutoSizeImageView(Context context) {
        super(context);
    }

    public AutoSizeImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoSizeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageUrl(String url) {
        Picasso.with(getContext()).load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                int imageWidth = bitmap.getWidth();
                int imageHeight = bitmap.getHeight();

                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.height = layoutParams.width * imageHeight / imageWidth;

                setImageBitmap(bitmap);

                requestLayout();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }
}
