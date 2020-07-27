package com.mobitv.ott.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.mobitv.ott.database.dao.SearchHistoryDao;
import com.mobitv.ott.database.dao.WatchHistoryDao;
import com.mobitv.ott.database.entity.SearchHistoryEntity;
import com.mobitv.ott.database.entity.WatchHistoryEntity;

@Database(entities = {WatchHistoryEntity.class, SearchHistoryEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract WatchHistoryDao watchHistoryDao();
    public abstract SearchHistoryDao searchHistoryDao();
    static final String DATABASE_NAME = "vivaTV-db";
}
