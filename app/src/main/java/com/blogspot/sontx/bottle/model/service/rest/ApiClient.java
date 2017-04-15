package com.blogspot.sontx.bottle.model.service.rest;

import com.blogspot.sontx.bottle.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public final class ApiClient {
    private static Retrofit retrofitUnauth = null;
    private static Retrofit retrofitAuth = null;
    private static Retrofit retrofitFsAuth = null;

    private ApiClient() {
    }

    public static synchronized Retrofit getClient() {
        if (retrofitUnauth == null) {
            String baseUrl = System.getProperty(Constants.BOTTLE_SERVER_BASE_URL_KEY);
            retrofitUnauth = new Retrofit
                    .Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }
        return retrofitUnauth;
    }

    public static synchronized Retrofit getClient(final String token) {
        if (retrofitAuth == null) {
            String baseUrl = System.getProperty(Constants.BOTTLE_SERVER_BASE_URL_KEY);
            retrofitAuth = getClient(token, baseUrl);
        }
        return retrofitAuth;
    }

    public static synchronized Retrofit getFsClient(final String token) {
        if (retrofitFsAuth == null) {
            String baseUrl = System.getProperty(Constants.BOTTLE_FS_BASE_URL_KEY);
            retrofitFsAuth = getClient(token, baseUrl);
        }
        return retrofitFsAuth;
    }

    private static synchronized Retrofit getClient(final String token, String baseUrl) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request authorization = chain.request().newBuilder().addHeader("Authorization", "Bearer " + token).build();
                return chain.proceed(authorization);
            }
        }).build();

        return new Retrofit
                .Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }
}
