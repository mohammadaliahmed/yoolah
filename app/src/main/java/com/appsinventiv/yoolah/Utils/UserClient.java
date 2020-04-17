package com.appsinventiv.yoolah.Utils;


import com.appsinventiv.yoolah.NetworkResponses.AllRoomMessagesResponse;
import com.appsinventiv.yoolah.NetworkResponses.GetPollResponse;
import com.appsinventiv.yoolah.NetworkResponses.LoginResponse;
import com.appsinventiv.yoolah.NetworkResponses.NewMessageResponse;
import com.appsinventiv.yoolah.NetworkResponses.PollAnswersResponse;
import com.appsinventiv.yoolah.NetworkResponses.RoomDetailsResponse;
import com.appsinventiv.yoolah.NetworkResponses.RoomInfoResponse;
import com.appsinventiv.yoolah.NetworkResponses.SignupResponse;
import com.appsinventiv.yoolah.NetworkResponses.UserMessagesResponse;
import com.appsinventiv.yoolah.NetworkResponses.UserProfileResponse;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserClient {


    @Headers("Content-Type: application/json")
    @POST("api/user/userProfile")
    Call<UserProfileResponse> userProfile(
            @Body JsonObject jsonObject

    );

    @POST("api/user/login")
    @FormUrlEncoded
    Call<LoginResponse> loginUser(
            @Field("api_username") String api_username,
            @Field("api_password") String api_password,
            @Field("email") String email,
            @Field("password") String password

    );


    @Headers("Content-Type: application/json")
    @POST("api/user/register")
    Call<SignupResponse> register(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/user/updateProfile")
    Call<UserProfileResponse> updateProfile(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/room/updateCoverUrl")
    Call<ResponseBody> updateCoverUrl(
            @Body JsonObject jsonObject

    );


    @POST("api/uploadFile")
    @Multipart
    Call<ResponseBody> uploadFile(
            @Part MultipartBody.Part file, @Part("photo") RequestBody name

    );

    @POST("api/uploadFile")
    @Multipart
    Call<ResponseBody> uploadAudioFile(
            @Part MultipartBody.Part file, @Part("audio") RequestBody name

    );

    @POST("api/uploadFile")
    @Multipart
    Call<ResponseBody> uploadDocumentFile(
            @Part MultipartBody.Part file, @Part("document") RequestBody name, @Part("extension") RequestBody extension

    );

    @POST("api/uploadFile")
    @Multipart
    Call<ResponseBody> uploadVideoFile(
            @Part MultipartBody.Part file, @Part("video") RequestBody name

    );


    @Headers("Content-Type: application/json")
    @POST("api/message/createMessage")
    Call<AllRoomMessagesResponse> createMessage(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/poll/createPoll")
    Call<ResponseBody> createPoll(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/message/allRoomMessages")
    Call<AllRoomMessagesResponse> allRoomMessages(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/message/deleteMessageFroAll")
    Call<AllRoomMessagesResponse> deleteMessageFroAll(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/poll/getPoll")
    Call<GetPollResponse> getPoll(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/poll/submitAnswer")
    Call<ResponseBody> submitAnswer(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/poll/getAllPolls")
    Call<PollAnswersResponse> getAllPolls(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/poll/deletePoll")
    Call<ResponseBody> deletePoll(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/poll/getAllPollsToFill")
    Call<PollAnswersResponse> getAllPollsToFill(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/room/getRoomDetails")
    Call<RoomDetailsResponse> getRoomDetails(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/room/addUserToRoom")
    Call<RoomInfoResponse> addUserToRoom(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/room/addUserToRoomWithRoomId")
    Call<RoomInfoResponse> addUserToRoomWithRoomId(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/room/getRoomDetailsFromID")
    Call<RoomDetailsResponse> getRoomDetailsFromID(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/user/updateFcmKey")
    Call<LoginResponse> updateFcmKey(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/room/getRoomInfo")
    Call<RoomInfoResponse> getRoomInfo(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/room/removeParticipant")
    Call<RoomInfoResponse> removeParticipant(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/message/userMessages")
    Call<UserMessagesResponse> userMessages(
            @Body JsonObject jsonObject

    );


}
