package com.mobitv.ott.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_watch_history")
public class WatchHistoryEntity extends CommonEntity{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private int userID;
//id
    @ColumnInfo(name = "item_id")
    private int itemID;
//loai phim
    @ColumnInfo(name = "item_type")
    private String itemType;

    //tap phim
    @ColumnInfo(name = "episode_id")
    private int episodeID;
    //vi tri hien tai
    @ColumnInfo(name = "current_position")
    private long currentPosition;
    //ngay xem
    @ColumnInfo(name = "watch_date")
    private long watchDate;

    public WatchHistoryEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public int getEpisodeID() {
        return episodeID;
    }

    public void setEpisodeID(int episodeID) {
        this.episodeID = episodeID;
    }

    public long getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(long currentPosition) {
        this.currentPosition = currentPosition;
    }

    public long getWatchDate() {
        return watchDate;
    }

    public void setWatchDate(long watchDate) {
        this.watchDate = watchDate;
    }
}
