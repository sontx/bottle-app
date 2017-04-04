package com.blogspot.sontx.bottle.model.service.rest;

import com.blogspot.sontx.bottle.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public final class ApiClient {
    private static Retrofit retrofit = null;

    private ApiClient() {
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            String baseUrl = System.getProperty(Constants.BOTTLE_SERVER_BASE_URL_KEY);
            retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(JacksonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
