package com.blogspot.sontx.bottle.model.bean;

import lombok.Data;

@Data
public class Animal {
    private String name;
    private String imageUrl;

    public Animal(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
