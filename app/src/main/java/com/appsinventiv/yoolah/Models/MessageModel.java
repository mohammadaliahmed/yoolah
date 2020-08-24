package com.appsinventiv.yoolah.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("messageText")
    @Expose
    private String messageText;
    @SerializedName("messageType")
    @Expose
    private String messageType;
    @SerializedName("messageByName")
    @Expose
    private String messageByName;
    @SerializedName("documentUrl")
    @Expose
    private String documentUrl;
    @SerializedName("messageById")
    @Expose
    private Integer messageById;
    @SerializedName("roomId")
    @Expose
    private Integer roomId;
    @SerializedName("time")
    @Expose
    private Long time;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("audioUrl")
    @Expose
    private String audioUrl;
    @SerializedName("videoUrl")
    @Expose
    private String videoUrl;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("messageByPicUrl")
    @Expose
    private String messageByPicUrl;
    @SerializedName("mediaTime")
    @Expose
    private long mediaTime;
    @SerializedName("filename")
    @Expose
    private String filename;
    @SerializedName("messageUploaded")
    @Expose
    private boolean messageUploaded = true;
    String localPath;


    public MessageModel(Integer id, String messageText, String messageType,
                        Integer messageById, Integer roomId, Long time, boolean messageUploaded, String localPath) {
        this.id = id;
        this.messageText = messageText;
        this.messageType = messageType;
        this.messageById = messageById;
        this.roomId = roomId;
        this.time = time;
        this.messageUploaded = messageUploaded;
        this.localPath = localPath;
    }

    public MessageModel(int id,String messageText, String messageType,long time) {
        this.messageText = messageText;
        this.id = id;
        this.messageType = messageType;
        this.time = time;
    }

    public MessageModel(Integer id, String messageText, String messageType,
                        Integer messageById, Integer roomId, Long time, boolean messageUploaded, String localPath, String filename) {
        this.id = id;
        this.messageText = messageText;
        this.messageType = messageType;
        this.messageById = messageById;
        this.roomId = roomId;
        this.time = time;
        this.messageUploaded = messageUploaded;
        this.localPath = localPath;
        this.filename = filename;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public long getMediaTime() {
        return mediaTime;
    }

    public void setMediaTime(long mediaTime) {
        this.mediaTime = mediaTime;
    }

    public String getMessageByPicUrl() {
        return messageByPicUrl;
    }

    public void setMessageByPicUrl(String messageByPicUrl) {
        this.messageByPicUrl = messageByPicUrl;
    }

    public boolean isMessageUploaded() {
        return messageUploaded;
    }

    public void setMessageUploaded(boolean messageUploaded) {
        this.messageUploaded = messageUploaded;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageByName() {
        return messageByName;
    }

    public void setMessageByName(String messageByName) {
        this.messageByName = messageByName;
    }

    public Integer getMessageById() {
        return messageById;
    }

    public void setMessageById(Integer messageById) {
        this.messageById = messageById;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
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
