
package com.appsinventiv.yoolah.NetworkResponses;

import com.appsinventiv.yoolah.Models.UserMessages;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserMessagesResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("messages")
    @Expose
    private List<UserMessages> messages = null;

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

    public List<UserMessages> getMessages() {
        return messages;
    }

    public void setMessages(List<UserMessages> messages) {
        this.messages = messages;
    }
}
