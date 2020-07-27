package com.mobitv.ott.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LiveTvChannelModel implements Parcelable {

    @SerializedName("icon_url")
    private String iconUrl;

    @SerializedName("is_octoshape")
    private int isOctoshape;

    @SerializedName("encryption_url")
    private String encryptionUrl;

    @SerializedName("pin_protected")
    private String pinProtected;

    @SerializedName("title")
    private String title;

    @SerializedName("genre_id")
    private int genreId;

    @SerializedName("channel_number")
    private int channelNumber;

    @SerializedName("token")
    private int token;

    @SerializedName("drm_platform")
    private String drmPlatform;

    @SerializedName("encryption")
    private int encryption;

    @SerializedName("favorite_channel")
    private String favoriteChannel;

    @SerializedName("channel_mode")
    private String channelMode;

    @SerializedName("id")
    private int id;

    @SerializedName("canWatched")
    private boolean canWatched = true;

    @SerializedName("stream_format")
    private String streamFormat;

    @SerializedName("stream_source_id")
    private int streamSourceId;

    @SerializedName("token_url")
    private String tokenUrl;

    @SerializedName("stream_url")
    private String streamUrl;

    @SerializedName("epgtoday")
    private List<DailyEPGModel> dailyEPGModelList;

    public LiveTvChannelModel() {
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIsOctoshape(int isOctoshape) {
        this.isOctoshape = isOctoshape;
    }

    public int getIsOctoshape() {
        return isOctoshape;
    }

    public void setEncryptionUrl(String encryptionUrl) {
        this.encryptionUrl = encryptionUrl;
    }

    public String getEncryptionUrl() {
        return encryptionUrl;
    }

    public void setPinProtected(String pinProtected) {
        this.pinProtected = pinProtected;
    }

    public String getPinProtected() {
        return pinProtected;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public int getGenreId() {
        return genreId;
    }

    public void setChannelNumber(int channelNumber) {
        this.channelNumber = channelNumber;
    }

    public int getChannelNumber() {
        return channelNumber;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public int getToken() {
        return token;
    }

    public void setDrmPlatform(String drmPlatform) {
        this.drmPlatform = drmPlatform;
    }

    public String getDrmPlatform() {
        return drmPlatform;
    }

    public void setEncryption(int encryption) {
        this.encryption = encryption;
    }

    public int getEncryption() {
        return encryption;
    }

    public void setFavoriteChannel(String favoriteChannel) {
        this.favoriteChannel = favoriteChannel;
    }

    public String getFavoriteChannel() {
        return favoriteChannel;
    }

    public void setChannelMode(String channelMode) {
        this.channelMode = channelMode;
    }

    public String getChannelMode() {
        return channelMode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    public void setCanWatched(boolean canWatched){
        this.canWatched = canWatched;
    }

    public boolean isCanWatched(){
        return canWatched;
    }

    public void setStreamFormat(String streamFormat) {
        this.streamFormat = streamFormat;
    }

    public String getStreamFormat() {
        return streamFormat;
    }

    public void setStreamSourceId(int streamSourceId) {
        this.streamSourceId = streamSourceId;
    }

    public int getStreamSourceId() {
        return streamSourceId;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public List<DailyEPGModel> getDailyEPGModelList() {
        return dailyEPGModelList;
    }

    public void setDailyEPGModelList(List<DailyEPGModel> dailyEPGModelList) {
        this.dailyEPGModelList = dailyEPGModelList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.iconUrl);
        dest.writeInt(this.isOctoshape);
        dest.writeString(this.encryptionUrl);
        dest.writeString(this.pinProtected);
        dest.writeString(this.title);
        dest.writeInt(this.genreId);
        dest.writeInt(this.channelNumber);
        dest.writeInt(this.token);
        dest.writeString(this.drmPlatform);
        dest.writeInt(this.encryption);
        dest.writeString(this.favoriteChannel);
        dest.writeString(this.channelMode);
        dest.writeInt(this.id);
        dest.writeByte(this.canWatched ? (byte) 1 : (byte) 0);
        dest.writeString(this.streamFormat);
        dest.writeInt(this.streamSourceId);
        dest.writeString(this.tokenUrl);
        dest.writeString(this.streamUrl);
        dest.writeList(this.dailyEPGModelList);
    }

    protected LiveTvChannelModel(Parcel in) {
        this.iconUrl = in.readString();
        this.isOctoshape = in.readInt();
        this.encryptionUrl = in.readString();
        this.pinProtected = in.readString();
        this.title = in.readString();
        this.genreId = in.readInt();
        this.channelNumber = in.readInt();
        this.token = in.readInt();
        this.drmPlatform = in.readString();
        this.encryption = in.readInt();
        this.favoriteChannel = in.readString();
        this.channelMode = in.readString();
        this.id = in.readInt();
        this.canWatched = in.readByte() != 0;
        this.streamFormat = in.readString();
        this.streamSourceId = in.readInt();
        this.tokenUrl = in.readString();
        this.streamUrl = in.readString();
        this.dailyEPGModelList = new ArrayList<DailyEPGModel>();
        in.readList(this.dailyEPGModelList, DailyEPGModel.class.getClassLoader());
    }

    public static final Creator<LiveTvChannelModel> CREATOR = new Creator<LiveTvChannelModel>() {
        @Override
        public LiveTvChannelModel createFromParcel(Parcel source) {
            return new LiveTvChannelModel(source);
        }

        @Override
        public LiveTvChannelModel[] newArray(int size) {
            return new LiveTvChannelModel[size];
        }
    };
}