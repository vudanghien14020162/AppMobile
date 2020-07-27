package com.mobitv.ott.model;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TvSeason {

    @SerializedName("icon_url")
    private String iconUrl;

    @SerializedName("year")
    private int year;

    @SerializedName("largeimage")
    private String largeimage;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("icon")
    private String icon;

    @SerializedName("description")
    private String description;

    @SerializedName("pin_protected")
    private int pinProtected;

    @SerializedName("title")
    private String title;

    @SerializedName("trailer_url")
    private String trailerUrl;

    @SerializedName("rate")
    private int rate;

    @SerializedName("canWatched")
    private boolean canWatched;

    @SerializedName("vod_parent_id")
    private int vodParentId;

    @SerializedName("starring")
    private String starring;

    @SerializedName("director")
    private String director;

    @SerializedName("vod_type")
    private String vodType;

    @SerializedName("id")
    private int id;

    @SerializedName("content_rating")
    private int contentRating;

    @SerializedName("total_episode")
    private int totalEpisode;

    @SerializedName("current_episode")
    private int currentEpisode;

    @SerializedName("isFavorited")
    private int isFavourite;

    @SerializedName("episodes")
    private List<Episode> episodes;

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public void setLargeimage(String largeimage) {
        this.largeimage = largeimage;
    }

    public String getLargeimage() {
        return largeimage;
    }

    public void setImageUrl(String imageUrl,Context context) {
        ;
        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setPinProtected(int pinProtected) {
        this.pinProtected = pinProtected;
    }

    public int getPinProtected() {
        return pinProtected;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getRate() {
        return rate;
    }

    public void setVodParentId(int vodParentId) {
        this.vodParentId = vodParentId;
    }

    public int getVodParentId() {
        return vodParentId;
    }

    public void setStarring(String starring) {
        this.starring = starring;
    }

    public String getStarring() {
        return starring;
    }

    public void setVodType(String vodType) {
        this.vodType = vodType;
    }

    public String getVodType() {
        return vodType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getContentRating() {
        return contentRating;
    }

    public void setContentRating(int contentRating) {
        this.contentRating = contentRating;
    }

    public int getTotalEpisode() {
        return totalEpisode;
    }

    public void setTotalEpisode(int totalEpisode) {
        this.totalEpisode = totalEpisode;
    }

    public int getCurrentEpisode() {
        return currentEpisode;
    }

    public void setCurrentEpisode(int currentEpisode) {
        this.currentEpisode = currentEpisode;
    }

    public int getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(int isFavourite) {
        this.isFavourite = isFavourite;
    }

    public void setCanWatched(boolean canWatched) {
        this.canWatched = canWatched;
    }

    public boolean isCanWatched() {
        return canWatched;
    }
}