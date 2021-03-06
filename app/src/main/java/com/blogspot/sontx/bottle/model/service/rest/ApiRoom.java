package com.blogspot.sontx.bottle.model.service.rest;

import com.blogspot.sontx.bottle.model.bean.Room;
import com.blogspot.sontx.bottle.model.bean.RoomMessage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiRoom {
    @GET("rooms/{roomId}")
    Call<Room> getRoom(@Path("roomId") int roomId);

    @GET("rooms/{roomId}/messages")
    Call<List<RoomMessage>> getRoomMessages(@Path("roomId") int roomId, @Query("page") int page, @Query("pageSize") int pageSize);

    @POST("rooms/{roomId}/messages")
    Call<RoomMessage> postRoomMessage(@Path("roomId") int roomId, @Body RoomMessage roomMessage);

    @PUT("rooms/messages/{messageId}")
    Call<RoomMessage> editMessage(@Path("messageId") int messageId, @Body RoomMessage roomMessage);

    @GET("rooms/messages/{messageId}")
    Call<RoomMessage> getMessage(@Path("messageId") int messageId);

    @DELETE("rooms/messages/{messageId}")
    Call<RoomMessage> deleteMessage(@Path("messageId") int messageId);
}
