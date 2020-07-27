package com.mobitv.ott.database;

import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.RoomOpenHelper.ValidationResult;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import com.mobitv.ott.database.dao.SearchHistoryDao;
import com.mobitv.ott.database.dao.SearchHistoryDao_Impl;
import com.mobitv.ott.database.dao.WatchHistoryDao;
import com.mobitv.ott.database.dao.WatchHistoryDao_Impl;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile WatchHistoryDao _watchHistoryDao;

  private volatile SearchHistoryDao _searchHistoryDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `tbl_watch_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `user_id` INTEGER NOT NULL, `item_id` INTEGER NOT NULL, `item_type` TEXT, `episode_id` INTEGER NOT NULL, `current_position` INTEGER NOT NULL, `watch_date` INTEGER NOT NULL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `tbl_search_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `user_id` INTEGER NOT NULL, `keyword` TEXT, `keyword_khongdau` TEXT, `search_date` INTEGER NOT NULL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '1a914dc6400b44a5622ed5fdc4fb6fe2')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `tbl_watch_history`");
        _db.execSQL("DROP TABLE IF EXISTS `tbl_search_history`");
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onDestructiveMigration(_db);
          }
        }
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      protected RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsTblWatchHistory = new HashMap<String, TableInfo.Column>(7);
        _columnsTblWatchHistory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTblWatchHistory.put("user_id", new TableInfo.Column("user_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTblWatchHistory.put("item_id", new TableInfo.Column("item_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTblWatchHistory.put("item_type", new TableInfo.Column("item_type", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTblWatchHistory.put("episode_id", new TableInfo.Column("episode_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTblWatchHistory.put("current_position", new TableInfo.Column("current_position", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTblWatchHistory.put("watch_date", new TableInfo.Column("watch_date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTblWatchHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTblWatchHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTblWatchHistory = new TableInfo("tbl_watch_history", _columnsTblWatchHistory, _foreignKeysTblWatchHistory, _indicesTblWatchHistory);
        final TableInfo _existingTblWatchHistory = TableInfo.read(_db, "tbl_watch_history");
        if (! _infoTblWatchHistory.equals(_existingTblWatchHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "tbl_watch_history(com.mobitv.ott.database.entity.WatchHistoryEntity).\n"
                  + " Expected:\n" + _infoTblWatchHistory + "\n"
                  + " Found:\n" + _existingTblWatchHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsTblSearchHistory = new HashMap<String, TableInfo.Column>(5);
        _columnsTblSearchHistory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTblSearchHistory.put("user_id", new TableInfo.Column("user_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTblSearchHistory.put("keyword", new TableInfo.Column("keyword", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTblSearchHistory.put("keyword_khongdau", new TableInfo.Column("keyword_khongdau", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTblSearchHistory.put("search_date", new TableInfo.Column("search_date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTblSearchHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTblSearchHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTblSearchHistory = new TableInfo("tbl_search_history", _columnsTblSearchHistory, _foreignKeysTblSearchHistory, _indicesTblSearchHistory);
        final TableInfo _existingTblSearchHistory = TableInfo.read(_db, "tbl_search_history");
        if (! _infoTblSearchHistory.equals(_existingTblSearchHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "tbl_search_history(com.mobitv.ott.database.entity.SearchHistoryEntity).\n"
                  + " Expected:\n" + _infoTblSearchHistory + "\n"
                  + " Found:\n" + _existingTblSearchHistory);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "1a914dc6400b44a5622ed5fdc4fb6fe2", "491d0a31d2a865666458f752d807a303");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "tbl_watch_history","tbl_search_history");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `tbl_watch_history`");
      _db.execSQL("DELETE FROM `tbl_search_history`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  public WatchHistoryDao watchHistoryDao() {
    if (_watchHistoryDao != null) {
      return _watchHistoryDao;
    } else {
      synchronized(this) {
        if(_watchHistoryDao == null) {
          _watchHistoryDao = new WatchHistoryDao_Impl(this);
        }
        return _watchHistoryDao;
      }
    }
  }

  @Override
  public SearchHistoryDao searchHistoryDao() {
    if (_searchHistoryDao != null) {
      return _searchHistoryDao;
    } else {
      synchronized(this) {
        if(_searchHistoryDao == null) {
          _searchHistoryDao = new SearchHistoryDao_Impl(this);
        }
        return _searchHistoryDao;
      }
    }
  }
}
