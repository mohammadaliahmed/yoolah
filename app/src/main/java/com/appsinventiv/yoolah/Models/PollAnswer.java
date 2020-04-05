package com.appsinventiv.yoolah.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PollAnswer {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("pollId")
    @Expose
    private Integer pollId;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("option")
    @Expose
    private Integer option;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPollId() {
        return pollId;
    }

    public void setPollId(Integer pollId) {
        this.pollId = pollId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getOption() {
        return option;
    }

    public void setOption(Integer option) {
        this.option = option;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
