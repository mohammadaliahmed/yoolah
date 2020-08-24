
package com.appsinventiv.yoolah.NetworkResponses;

import com.appsinventiv.yoolah.Database.Word;
import com.appsinventiv.yoolah.Models.RoomModel;
import com.appsinventiv.yoolah.Models.UserModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessageInfoResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("users")
    @Expose
    private List<UserModel> users = null;
    @SerializedName("messageObject")
    @Expose
    private Word messageObject;


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

    public List<UserModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserModel> users) {
        this.users = users;
    }

    public Word getMessageObject() {
        return messageObject;
    }

    public void setMessageObject(Word messageObject) {
        this.messageObject = messageObject;
    }
}
