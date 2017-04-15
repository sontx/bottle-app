package com.blogspot.sontx.bottle.model.service.rest;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiFileStream {
    @Multipart
    @POST("storage")
    Call<ResponseBody> upload(@Part MultipartBody.Part file);
}
