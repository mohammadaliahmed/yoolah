package com.appsinventiv.yoolah.NetworkResponses;

import com.appsinventiv.yoolah.Models.PollAnswer;
import com.appsinventiv.yoolah.Models.PollModel;
import com.appsinventiv.yoolah.Models.UserModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class GetPollResponse {


    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("poll")
    @Expose
    private PollModel pollModel;
    @SerializedName("pollAnswer")
    @Expose
    private PollAnswer pollAnswer;


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

    public PollModel getPollModel() {
        return pollModel;
    }

    public void setPollModel(PollModel pollModel) {
        this.pollModel = pollModel;
    }

    public PollAnswer getPollAnswer() {
        return pollAnswer;
    }

    public void setPollAnswer(PollAnswer pollAnswer) {
        this.pollAnswer = pollAnswer;
    }
}
