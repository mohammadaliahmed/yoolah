
package com.appsinventiv.yoolah.NetworkResponses;

import com.appsinventiv.yoolah.Database.Word;
import com.appsinventiv.yoolah.Models.MessageModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllRoomMessagesResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("messages")
    @Expose
    private List<MessageModel> messages = null;
    @SerializedName("messageModel")
    @Expose
    private Word messageModel = null;

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

    public List<MessageModel> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageModel> messages) {
        this.messages = messages;
    }

    public Word getMessageModel() {
        return messageModel;
    }

    public void setMessageModel(Word messageModel) {
        this.messageModel = messageModel;
    }
}
