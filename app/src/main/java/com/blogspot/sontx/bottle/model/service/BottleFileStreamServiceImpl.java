package com.blogspot.sontx.bottle.model.service;

import android.util.Log;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.model.bean.BottleUser;
import com.blogspot.sontx.bottle.model.bean.UploadResult;
import com.blogspot.sontx.bottle.model.service.interfaces.BottleFileStreamService;
import com.blogspot.sontx.bottle.model.service.rest.ApiClient;
import com.blogspot.sontx.bottle.model.service.rest.ApiFileStream;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

class BottleFileStreamServiceImpl implements BottleFileStreamService {
    private static final String TAG = "BOTTLE-FS";

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void uploadAsync(String mediaPath, final Callback<UploadResult> callback) {
        if (!App.getInstance().getBottleContext().isLogged()) {
            callback.onError(new Exception("Unauthenticated"));
            return;
        }

        BottleUser bottleUser = App.getInstance().getBottleContext().getCurrentBottleUser();

        File file = new File(mediaPath);

        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody requestFile = RequestBody.create(mediaType, file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        ApiFileStream apiFileStream = ApiClient.getFsClient(bottleUser.getToken()).create(ApiFileStream.class);

        Call<ResponseBody> call = apiFileStream.upload(body);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        callback.onSuccess(objectMapper.readValue(response.body().byteStream(), UploadResult.class));
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                        callback.onError(e);
                    }
                } else {
                    Log.e(TAG, "uploadAsync: " + response.code() + " " + response.message());
                    callback.onError(new Exception(""));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}
