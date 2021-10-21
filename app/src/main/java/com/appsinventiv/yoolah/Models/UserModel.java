package com.appsinventiv.yoolah.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserModel {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("picUrl")
    @Expose
    private String picUrl;
    @SerializedName("thumbnailUrl")
    @Expose
    private String thumbnailUrl;
    @SerializedName("fcmKey")
    @Expose
    private String fcmKey;
    @SerializedName("friendsCount")
    @Expose
    private int friendsCount;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("randomcode")
    @Expose
    private String randomcode;
    @SerializedName("time")
    @Expose
    private long time;
    @SerializedName("can_message")
    @Expose
    private int canMessage;



    @SerializedName("requestsSent")
    @Expose
    private List<Integer> requestSentList;
    @SerializedName("requestsReceived")
    @Expose
    private List<Integer> requestsReceivedList;
    @SerializedName("friends")
    @Expose
    private List<Integer> friendsList;

    public int getCanMessage() {
        return canMessage;
    }

    public void setCanMessage(int canMessage) {
        this.canMessage = canMessage;
    }

    public String getRandomcode() {
        return randomcode;
    }

    public void setRandomcode(String randomcode) {
        this.randomcode = randomcode;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getFcmKey() {
        return fcmKey;
    }

    public void setFcmKey(String fcmKey) {
        this.fcmKey = fcmKey;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public List<Integer> getRequestSentList() {
        return requestSentList;
    }

    public void setRequestSentList(List<Integer> requestSentList) {
        this.requestSentList = requestSentList;
    }

    public List<Integer> getRequestsReceivedList() {
        return requestsReceivedList;
    }

    public void setRequestsReceivedList(List<Integer> requestsReceivedList) {
        this.requestsReceivedList = requestsReceivedList;
    }

    public List<Integer> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(List<Integer> friendsList) {
        this.friendsList = friendsList;
    }
}
