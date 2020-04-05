package com.appsinventiv.yoolah.NetworkResponses;

import com.appsinventiv.yoolah.Models.UserModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProfileResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("friendCount")
    @Expose
    private int friendCount;
    @SerializedName("user")
    @Expose
    private UserModel userModel;

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public int getFriendCount() {
        return friendCount;
    }

    public void setFriendCount(int friendCount) {
        this.friendCount = friendCount;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
