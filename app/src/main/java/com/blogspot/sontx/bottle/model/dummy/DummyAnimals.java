package com.blogspot.sontx.bottle.model.dummy;

import com.blogspot.sontx.bottle.model.bean.Animal;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public final class DummyAnimals {
    private static List<Animal> animals = null;
    private static Random random;

    private static void setup() {
        animals = new ArrayList<>();
        animals.add(new Animal("Bird", "file:///android_asset/avatar_bird.png"));
        animals.add(new Animal("Cat", "file:///android_asset/avatar_cat.png"));
        animals.add(new Animal("Chicken", "file:///android_asset/avatar_chicken.png"));
        animals.add(new Animal("Clown Fish", "file:///android_asset/avatar_clown_fish.png"));
        animals.add(new Animal("Cow", "file:///android_asset/avatar_cow.png"));
        animals.add(new Animal("Crab", "file:///android_asset/avatar_crab.png"));
        animals.add(new Animal("Dog", "file:///android_asset/avatar_dog.png"));
        animals.add(new Animal("Dolphin", "file:///android_asset/avatar_dolphin.png"));
        animals.add(new Animal("Duck", "file:///android_asset/avatar_duck.png"));
        animals.add(new Animal("Fish", "file:///android_asset/avatar_fish.png"));
        animals.add(new Animal("Gorilla", "file:///android_asset/avatar_gorilla.png"));
        animals.add(new Animal("Insect", "file:///android_asset/avatar_insect.png"));
        animals.add(new Animal("Octopus", "file:///android_asset/avatar_octopus.png"));
        animals.add(new Animal("Pig", "file:///android_asset/avatar_pig.png"));
        animals.add(new Animal("Prawn", "file:///android_asset/avatar_prawn.png"));
        animals.add(new Animal("Rabbit", "file:///android_asset/avatar_rabbit.png"));
        animals.add(new Animal("Seahorse", "file:///android_asset/avatar_seahorse.png"));
        animals.add(new Animal("Sheep", "file:///android_asset/avatar_sheep.png"));
        animals.add(new Animal("Spider", "file:///android_asset/avatar_spider.png"));
        animals.add(new Animal("Starfish", "file:///android_asset/avatar_starfish.png"));
        animals.add(new Animal("Stock", "file:///android_asset/avatar_stock.png"));
        animals.add(new Animal("Turtle", "file:///android_asset/avatar_turtle.png"));

        random = new Random(new Date().getTime());
    }

    public static PublicProfile mix(PublicProfile publicProfile) {
        if (animals == null)
            setup();

        int index = random.nextInt(animals.size());
        Animal animal = animals.get(index);

        publicProfile.setAvatarUrl(animal.getImageUrl());
        publicProfile.setDisplayName(animal.getName());

        return publicProfile;
    }

    private DummyAnimals() {
    }
}
