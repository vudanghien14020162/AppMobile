package com.mobitv.ott.database.dao;

import androidx.room.*;
import com.mobitv.ott.database.entity.WatchHistoryEntity;

import java.util.List;

@Dao
public interface WatchHistoryDao {
    @Query("SELECT * FROM tbl_watch_history ORDER BY watch_date DESC")
    List<WatchHistoryEntity> loadAllWatchHistory();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WatchHistoryEntity watchHistoryEntity);

    @Query("SELECT * FROM tbl_watch_history WHERE item_id = :videoID AND item_type = :videoType")
    WatchHistoryEntity loadWatchHistoryByVideo(int videoID, String videoType);

    @Update
    void update(WatchHistoryEntity watchHistoryEntity);

    @Query("SELECT * FROM tbl_watch_history WHERE item_id = :videoID AND item_type = :videoType AND episode_id = :episodeID")
    WatchHistoryEntity getCurrentPosition(int videoID, String videoType, int episodeID);
}
