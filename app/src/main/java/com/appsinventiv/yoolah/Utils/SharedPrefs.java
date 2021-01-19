package com.appsinventiv.yoolah.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.appsinventiv.yoolah.Database.WordRepository;
import com.appsinventiv.yoolah.Database.WordViewModel;
import com.appsinventiv.yoolah.Models.MessageModel;
import com.appsinventiv.yoolah.Models.RoomModel;
import com.appsinventiv.yoolah.Models.UserMessages;
import com.appsinventiv.yoolah.Models.UserModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.lifecycle.ViewModelProviders;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by AliAh on 20/02/2018.
 */

public class SharedPrefs {


    private SharedPrefs() {

    }

//
//    public static void setRequestsSentList(ArrayList<FriendsRequestModel> itemList) {
//
//        Gson gson = new Gson();
//        String json = gson.toJson(itemList);
//        preferenceSetter("FriendsRequestModel", json);
//    }
//
//    public static List<FriendsRequestModel> getRequestsSentList() {
//        Gson gson = new Gson();
//        List<FriendsRequestModel> playersList = (List<FriendsRequestModel>) gson.fromJson(preferenceGetter("FriendsRequestModel"),
//                new TypeToken<List<FriendsRequestModel>>() {
//                }.getType());
//        return playersList;
//    }

    //
    public static void setMuted(String value) {
        preferenceSetter("muted", value);
    }

    public static String getMuted() {
        return preferenceGetter("muted");
    }

//    public static void setPickedList(ArrayList<StoriesPickedModel> itemList) {
//
//        Gson gson = new Gson();
//        String json = gson.toJson(itemList);
//        preferenceSetter("storiesPicked", json);
//    }
//
//    public static ArrayList getPickedList() {
//        Gson gson = new Gson();
//        ArrayList<StoriesPickedModel> playersList = (ArrayList<StoriesPickedModel>) gson.fromJson(preferenceGetter("storiesPicked"),
//                new TypeToken<ArrayList<StoriesPickedModel>>() {
//                }.getType());
//        return playersList;
//    }
//
//    public static void setHomeList(List<PostModel> itemList) {
//
//        Gson gson = new Gson();
//        String json = gson.toJson(itemList);
//        preferenceSetter("setHomeList", json);
//    }
//
//    public static List getHomeList() {
//        Gson gson = new Gson();
//        List<PostModel> playersList = (List<PostModel>) gson.fromJson(preferenceGetter("setHomeList"),
//                new TypeToken<List<PostModel>>() {
//                }.getType());
//        return playersList;
//    }

    public static void setRoomDetails(HashMap<Integer, RoomModel> itemList) {

        Gson gson = new Gson();
        String json = gson.toJson(itemList);
        preferenceSetter("setRoomDetails", json);
    }

    public static HashMap<Integer, RoomModel> getRoomDetails() {
        Gson gson = new Gson();

        HashMap<Integer, RoomModel> retMap = new Gson().fromJson(
                preferenceGetter("setRoomDetails"), new TypeToken<HashMap<Integer, RoomModel>>() {
                }.getType()
        );

        return retMap;
    }

    public static void clearCartMenuIds() {
        Gson gson = new Gson();
        String json = "";
        preferenceSetter("cartMenu", json);


    }

    public static void setInsideMessages(HashMap<Integer, List<MessageModel>> map, int roomid) {

        Gson gson = new Gson();
        String json = gson.toJson(map);
        preferenceSetter("setInsideMessages" + roomid, json);
    }

    public static HashMap<Integer, List<MessageModel>> getInsideMessages(int roomId) {
        Gson gson = new Gson();

        HashMap<Integer, List<MessageModel>> retMap = new Gson().fromJson(
                preferenceGetter("setInsideMessages" + roomId), new TypeToken<HashMap<Integer, List<MessageModel>>>() {
                }.getType()
        );

        return retMap;
    }

    public static void setLastSeenMessage(HashMap<Integer, Boolean> itemList, int roomId) {

        Gson gson = new Gson();
        String json = gson.toJson(itemList);
        preferenceSetter("setLastSeenMessage" + roomId, json);
    }

    public static HashMap<Integer, Boolean> getLastSeenMessage(int roomId) {
        Gson gson = new Gson();

        HashMap<Integer, Boolean> retMap = new Gson().fromJson(
                preferenceGetter("setLastSeenMessage" + roomId), new TypeToken<HashMap<Integer, Boolean>>() {
                }.getType()
        );

        return retMap;
    }

    public static void setHomeMessages(HashMap<Integer, UserMessages> itemList) {

        Gson gson = new Gson();
        String json = gson.toJson(itemList);
        preferenceSetter("setHomeMessages", json);
    }

    public static HashMap<Integer, UserMessages> getHomeMessages() {
        Gson gson = new Gson();

        HashMap<Integer, UserMessages> retMap = new Gson().fromJson(
                preferenceGetter("setHomeMessages"), new TypeToken<HashMap<Integer, UserMessages>>() {
                }.getType()
        );

        return retMap;
    }

    public static void clearTableIds() {
        Gson gson = new Gson();
        String json = "";
        preferenceSetter("tableId", json);


    }

    public static void setToken(String token) {
        preferenceSetter("token", token);
    }

    public static String getToken() {
        return preferenceGetter("token");
    }

    public static void setLastDate(String token, Integer roomId) {
        preferenceSetter(roomId + "setLastDate", token);
    }

    public static String getLastDate(Integer roomId) {
        return preferenceGetter(roomId + "setLastDate");
    }

    public static void setRoomId(String token) {
        preferenceSetter("setRoomId", token);
    }

    public static String getRoomId() {
        return preferenceGetter("setRoomId");
    }

    public static void setQrId(String token) {
        preferenceSetter("setQrId", token);
    }

    public static String getQrId() {
        return preferenceGetter("setQrId");
    }


    public static UserModel getParticipantModel(String userId) {
        Gson gson = new Gson();
        UserModel model = gson.fromJson(preferenceGetter(userId), UserModel.class);
        return model;
    }


    public static void setUserModel(UserModel model) {

        Gson gson = new Gson();
        String json = gson.toJson(model);
        preferenceSetter("UserModel", json);
    }

    public static UserModel getUserModel() {
        Gson gson = new Gson();
        UserModel model = gson.fromJson(preferenceGetter("UserModel"), UserModel.class);
        return model;
    }


//    public static void setHomeStories(ArrayList<ArrayList<StoryModel>> itemList) {
//
//        Gson gson = new Gson();
//        String json = gson.toJson(itemList);
//        preferenceSetter("getHomeStories", json);
//    }
//
//    public static ArrayList<ArrayList<StoryModel>> getHomeStories() {
//        Gson gson = new Gson();
//        ArrayList<ArrayList<StoryModel>> playersList = (ArrayList<ArrayList<StoryModel>>) gson.fromJson(preferenceGetter("getHomeStories"),
//                new TypeToken<ArrayList<ArrayList<StoryModel>>>() {
//                }.getType());
//
//        return playersList;
//    }
//
//    public static void setPosts(List<PostModel> itemList) {
//
//        Gson gson = new Gson();
//        String json = gson.toJson(itemList);
//        preferenceSetter("PostsModel", json);
//    }
//
//    public static List<PostModel> getPosts() {
//        Gson gson = new Gson();
//        List<PostModel> playersList = (List<PostModel>) gson.fromJson(preferenceGetter("PostsModel"),
//                new TypeToken<List<PostModel>>() {
//                }.getType());
//        return playersList;
//    }

    public static void preferenceSetter(String key, String value) {
        SharedPreferences pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String preferenceGetter(String key) {
        SharedPreferences pref;
        String value = "";
        pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        value = pref.getString(key, "");
        return value;
    }


    public static void logout() {
        WordRepository mRepository = new WordRepository(ApplicationClass.getInstance());
        mRepository.deleteAll();
        SharedPreferences pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }

}
