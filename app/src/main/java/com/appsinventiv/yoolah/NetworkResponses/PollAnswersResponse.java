
package com.appsinventiv.yoolah.NetworkResponses;

import com.appsinventiv.yoolah.Models.PollModel;
import com.appsinventiv.yoolah.Models.RoomModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PollAnswersResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("polls")
    @Expose
    private List<PollModel> polls = null;

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

    public List<PollModel> getPolls() {
        return polls;
    }

    public void setPolls(List<PollModel> polls) {
        this.polls = polls;
    }
}
