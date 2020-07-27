package com.mobitv.ott.database.dao;

import androidx.room.*;
import com.mobitv.ott.database.entity.SearchHistoryEntity;

import java.util.List;

@Dao
public interface SearchHistoryDao {
    @Query("SELECT * FROM tbl_search_history  ORDER BY search_date DESC LIMIT 5")
    List<SearchHistoryEntity> loadAllSearch();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SearchHistoryEntity searchHistoryEntity);

    @Update
    void update(SearchHistoryEntity searchHistoryEntity);

    @Delete
    void deleteSearchHistory(SearchHistoryEntity searchHistoryEntity);

    @Query("DELETE FROM tbl_search_history")
    void deleteAll();

    @Query("SELECT * FROM tbl_search_history WHERE keyword_khongdau = :keywordKhongDau")
    SearchHistoryEntity loadSearchByKeyword(String keywordKhongDau);
}
