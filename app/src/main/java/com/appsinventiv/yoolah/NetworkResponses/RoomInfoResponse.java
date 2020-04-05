
package com.appsinventiv.yoolah.NetworkResponses;

import com.appsinventiv.yoolah.Models.RoomModel;
import com.appsinventiv.yoolah.Models.UserModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RoomInfoResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("canMessage")
    @Expose
    private int canMessage;
    @SerializedName("room")
    @Expose
    private RoomModel room = null;
    @SerializedName("users")
    @Expose
    private List<UserModel> users = null;
    @SerializedName("roomId")
    @Expose
    private int roomId;


    public List<UserModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserModel> users) {
        this.users = users;
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

    public RoomModel getRoom() {
        return room;
    }

    public void setRoom(RoomModel room) {
        this.room = room;
    }

    public int getCanMessage() {
        return canMessage;
    }

    public void setCanMessage(int canMessage) {
        this.canMessage = canMessage;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
}
