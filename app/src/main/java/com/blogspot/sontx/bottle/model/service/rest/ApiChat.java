
package com.blogspot.sontx.bottle.model.service.rest;

import com.blogspot.sontx.bottle.model.bean.chat.CreateChannelRequest;
import com.blogspot.sontx.bottle.model.bean.chat.CreateChannelResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiChat {
    @POST("chat/channels")
    Call<CreateChannelResult> createChannel(@Body CreateChannelRequest createChannelRequest);
}
