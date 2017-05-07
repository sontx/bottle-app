package com.blogspot.sontx.bottle.model.bean;

import lombok.Data;

@Data
public class Emotion {
    private int imageResId;
    private String text;

    public Emotion() {
    }

    public Emotion(int imageResId, String text) {
        this.imageResId = imageResId;
        this.text = text;
    }
}
