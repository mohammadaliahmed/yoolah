package com.appsinventiv.yoolah.Database;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "word_table")
public class Word {
    @SerializedName("id")
    @Expose
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;


    @SerializedName("messageText")
    @Expose
    @ColumnInfo(name = "messageText")

    private String messageText;
    @SerializedName("messageType")
    @Expose
    @ColumnInfo(name = "messageType")

    private String messageType;
    @SerializedName("messageByName")
    @Expose
    @ColumnInfo(name = "messageByName")

    private String messageByName;
    @SerializedName("documentUrl")
    @Expose
    @ColumnInfo(name = "documentUrl")

    private String documentUrl;
    @SerializedName("messageById")
    @Expose
    @ColumnInfo(name = "messageById")

    private Integer messageById;
    @SerializedName("roomId")
    @Expose
    @ColumnInfo(name = "roomId")

    private Integer roomId;



    @SerializedName("oldId")
    @Expose
    @ColumnInfo(name = "oldId")

    private Integer oldId;




    @SerializedName("serverId")
    @Expose
    @ColumnInfo(name = "serverId")

    private Integer serverId;
    @SerializedName("time")
    @Expose
    @ColumnInfo(name = "time")

    private Long time;
    @SerializedName("created_at")
    @Expose
    @ColumnInfo(name = "created_at")

    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    @ColumnInfo(name = "updated_at")

    private String updatedAt;
    @SerializedName("audioUrl")
    @Expose
    @ColumnInfo(name = "audioUrl")

    private String audioUrl;
    @SerializedName("videoUrl")
    @Expose
    @ColumnInfo(name = "videoUrl")

    private String videoUrl;
    @SerializedName("imageUrl")
    @Expose
    @ColumnInfo(name = "imageUrl")

    private String imageUrl;
    @SerializedName("messageByPicUrl")
    @Expose
    @ColumnInfo(name = "messageByPicUrl")

    private String messageByPicUrl;
    @SerializedName("groupPicUrl")
    @Expose
    @ColumnInfo(name = "groupPicUrl")

    private String groupPicUrl;
    @SerializedName("mediaTime")
    @Expose
    @ColumnInfo(name = "mediaTime")

    private long mediaTime;
    @SerializedName("roomName")
    @Expose
    @ColumnInfo(name = "roomName")

    private String roomName;
    @SerializedName("filename")
    @Expose
    @ColumnInfo(name = "filename")

    private String filename;
    @SerializedName("messageRead")
    @Expose
    @ColumnInfo(name = "messageRead")
    private boolean messageRead;


    public Word(Integer serverId, String messageText, String messageType, String messageByName, String documentUrl,
                Integer messageById, Integer roomId, Long time, String audioUrl, String videoUrl, String imageUrl,
                String messageByPicUrl, long mediaTime, String filename, String groupPicUrl, String roomName, boolean messageRead,int oldId) {
        this.messageText = messageText;
        this.messageType = messageType;
        this.messageByName = messageByName;
        this.documentUrl = documentUrl;
        this.messageById = messageById;
        this.roomId = roomId;
        this.time = time;
        this.serverId = serverId;
        this.audioUrl = audioUrl;
        this.videoUrl = videoUrl;
        this.imageUrl = imageUrl;
        this.messageByPicUrl = messageByPicUrl;
        this.mediaTime = mediaTime;
        this.filename = filename;
        this.groupPicUrl = groupPicUrl;
        this.messageRead = messageRead;
        this.roomName = roomName;
        this.oldId = oldId;
    }


    public Integer getOldId() {
        return oldId;
    }

    public void setOldId(Integer oldId) {
        this.oldId = oldId;
    }



    public String  getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public boolean isMessageRead() {
        return messageRead;
    }

    public void setMessageRead(boolean messageRead) {
        this.messageRead = messageRead;
    }

    public String getGroupPicUrl() {
        return groupPicUrl;
    }

    public void setGroupPicUrl(String groupPicUrl) {
        this.groupPicUrl = groupPicUrl;
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
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

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
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

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMessageByPicUrl() {
        return messageByPicUrl;
    }

    public void setMessageByPicUrl(String messageByPicUrl) {
        this.messageByPicUrl = messageByPicUrl;
    }

    public long getMediaTime() {
        return mediaTime;
    }

    public void setMediaTime(long mediaTime) {
        this.mediaTime = mediaTime;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }


    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
