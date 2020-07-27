package com.mobitv.ott.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_search_history")
public class SearchHistoryEntity extends CommonEntity{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private int userID;

    @ColumnInfo(name = "keyword")
    private String keyword;

    @ColumnInfo(name = "keyword_khongdau")
    private String keywordKhongDau;

    @ColumnInfo(name = "search_date")
    private long searchDate;

    public SearchHistoryEntity() {
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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeywordKhongDau() {
        return keywordKhongDau;
    }

    public void setKeywordKhongDau(String keywordKhongDau) {
        this.keywordKhongDau = keywordKhongDau;
    }

    public long getSearchDate() {
        return searchDate;
    }

    public void setSearchDate(long searchDate) {
        this.searchDate = searchDate;
    }
}
