package com.blogspot.sontx.bottle.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.Emotion;

import java.util.List;

public class EmotionAdapter extends ArrayAdapter<Emotion> {
    private final boolean hasText;

    public EmotionAdapter(Context context, List<Emotion> emotions, boolean hasText) {
        super(context, R.layout.item_emotion_without_text, emotions);
        this.hasText = hasText;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(hasText ? R.layout.item_emotion : R.layout.item_emotion_without_text, null);
        Emotion emotion = getItem(position);
        if (emotion != null) {
            ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
            if (imageView != null)
                imageView.setImageResource(emotion.getImageResId());

            TextView textView = (TextView) view.findViewById(R.id.text_view);
            if (textView != null)
                textView.setText(emotion.getText());
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_emotion, null);
        Emotion emotion = getItem(position);
        if (emotion != null) {
            ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
            if (imageView != null)
                imageView.setImageResource(emotion.getImageResId());

            TextView textView = (TextView) view.findViewById(R.id.text_view);
            if (textView != null)
                textView.setText(emotion.getText());
        }
        return view;
    }
}
