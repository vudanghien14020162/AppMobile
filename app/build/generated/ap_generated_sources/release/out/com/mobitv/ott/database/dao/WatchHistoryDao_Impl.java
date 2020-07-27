package com.mobitv.ott.database.dao;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.mobitv.ott.database.entity.WatchHistoryEntity;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class WatchHistoryDao_Impl implements WatchHistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WatchHistoryEntity> __insertionAdapterOfWatchHistoryEntity;

  private final EntityDeletionOrUpdateAdapter<WatchHistoryEntity> __updateAdapterOfWatchHistoryEntity;

  public WatchHistoryDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWatchHistoryEntity = new EntityInsertionAdapter<WatchHistoryEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `tbl_watch_history` (`id`,`user_id`,`item_id`,`item_type`,`episode_id`,`current_position`,`watch_date`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, WatchHistoryEntity value) {
        stmt.bindLong(1, value.getId());
        stmt.bindLong(2, value.getUserID());
        stmt.bindLong(3, value.getItemID());
        if (value.getItemType() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getItemType());
        }
        stmt.bindLong(5, value.getEpisodeID());
        stmt.bindLong(6, value.getCurrentPosition());
        stmt.bindLong(7, value.getWatchDate());
      }
    };
    this.__updateAdapterOfWatchHistoryEntity = new EntityDeletionOrUpdateAdapter<WatchHistoryEntity>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `tbl_watch_history` SET `id` = ?,`user_id` = ?,`item_id` = ?,`item_type` = ?,`episode_id` = ?,`current_position` = ?,`watch_date` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, WatchHistoryEntity value) {
        stmt.bindLong(1, value.getId());
        stmt.bindLong(2, value.getUserID());
        stmt.bindLong(3, value.getItemID());
        if (value.getItemType() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getItemType());
        }
        stmt.bindLong(5, value.getEpisodeID());
        stmt.bindLong(6, value.getCurrentPosition());
        stmt.bindLong(7, value.getWatchDate());
        stmt.bindLong(8, value.getId());
      }
    };
  }

  @Override
  public void insert(final WatchHistoryEntity watchHistoryEntity) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfWatchHistoryEntity.insert(watchHistoryEntity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final WatchHistoryEntity watchHistoryEntity) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfWatchHistoryEntity.handle(watchHistoryEntity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<WatchHistoryEntity> loadAllWatchHistory() {
    final String _sql = "SELECT * FROM tbl_watch_history ORDER BY watch_date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfUserID = CursorUtil.getColumnIndexOrThrow(_cursor, "user_id");
      final int _cursorIndexOfItemID = CursorUtil.getColumnIndexOrThrow(_cursor, "item_id");
      final int _cursorIndexOfItemType = CursorUtil.getColumnIndexOrThrow(_cursor, "item_type");
      final int _cursorIndexOfEpisodeID = CursorUtil.getColumnIndexOrThrow(_cursor, "episode_id");
      final int _cursorIndexOfCurrentPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "current_position");
      final int _cursorIndexOfWatchDate = CursorUtil.getColumnIndexOrThrow(_cursor, "watch_date");
      final List<WatchHistoryEntity> _result = new ArrayList<WatchHistoryEntity>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final WatchHistoryEntity _item;
        _item = new WatchHistoryEntity();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final int _tmpUserID;
        _tmpUserID = _cursor.getInt(_cursorIndexOfUserID);
        _item.setUserID(_tmpUserID);
        final int _tmpItemID;
        _tmpItemID = _cursor.getInt(_cursorIndexOfItemID);
        _item.setItemID(_tmpItemID);
        final String _tmpItemType;
        _tmpItemType = _cursor.getString(_cursorIndexOfItemType);
        _item.setItemType(_tmpItemType);
        final int _tmpEpisodeID;
        _tmpEpisodeID = _cursor.getInt(_cursorIndexOfEpisodeID);
        _item.setEpisodeID(_tmpEpisodeID);
        final long _tmpCurrentPosition;
        _tmpCurrentPosition = _cursor.getLong(_cursorIndexOfCurrentPosition);
        _item.setCurrentPosition(_tmpCurrentPosition);
        final long _tmpWatchDate;
        _tmpWatchDate = _cursor.getLong(_cursorIndexOfWatchDate);
        _item.setWatchDate(_tmpWatchDate);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public WatchHistoryEntity loadWatchHistoryByVideo(final int videoID, final String videoType) {
    final String _sql = "SELECT * FROM tbl_watch_history WHERE item_id = ? AND item_type = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, videoID);
    _argIndex = 2;
    if (videoType == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, videoType);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfUserID = CursorUtil.getColumnIndexOrThrow(_cursor, "user_id");
      final int _cursorIndexOfItemID = CursorUtil.getColumnIndexOrThrow(_cursor, "item_id");
      final int _cursorIndexOfItemType = CursorUtil.getColumnIndexOrThrow(_cursor, "item_type");
      final int _cursorIndexOfEpisodeID = CursorUtil.getColumnIndexOrThrow(_cursor, "episode_id");
      final int _cursorIndexOfCurrentPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "current_position");
      final int _cursorIndexOfWatchDate = CursorUtil.getColumnIndexOrThrow(_cursor, "watch_date");
      final WatchHistoryEntity _result;
      if(_cursor.moveToFirst()) {
        _result = new WatchHistoryEntity();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _result.setId(_tmpId);
        final int _tmpUserID;
        _tmpUserID = _cursor.getInt(_cursorIndexOfUserID);
        _result.setUserID(_tmpUserID);
        final int _tmpItemID;
        _tmpItemID = _cursor.getInt(_cursorIndexOfItemID);
        _result.setItemID(_tmpItemID);
        final String _tmpItemType;
        _tmpItemType = _cursor.getString(_cursorIndexOfItemType);
        _result.setItemType(_tmpItemType);
        final int _tmpEpisodeID;
        _tmpEpisodeID = _cursor.getInt(_cursorIndexOfEpisodeID);
        _result.setEpisodeID(_tmpEpisodeID);
        final long _tmpCurrentPosition;
        _tmpCurrentPosition = _cursor.getLong(_cursorIndexOfCurrentPosition);
        _result.setCurrentPosition(_tmpCurrentPosition);
        final long _tmpWatchDate;
        _tmpWatchDate = _cursor.getLong(_cursorIndexOfWatchDate);
        _result.setWatchDate(_tmpWatchDate);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public WatchHistoryEntity getCurrentPosition(final int videoID, final String videoType,
      final int episodeID) {
    final String _sql = "SELECT * FROM tbl_watch_history WHERE item_id = ? AND item_type = ? AND episode_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, videoID);
    _argIndex = 2;
    if (videoType == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, videoType);
    }
    _argIndex = 3;
    _statement.bindLong(_argIndex, episodeID);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfUserID = CursorUtil.getColumnIndexOrThrow(_cursor, "user_id");
      final int _cursorIndexOfItemID = CursorUtil.getColumnIndexOrThrow(_cursor, "item_id");
      final int _cursorIndexOfItemType = CursorUtil.getColumnIndexOrThrow(_cursor, "item_type");
      final int _cursorIndexOfEpisodeID = CursorUtil.getColumnIndexOrThrow(_cursor, "episode_id");
      final int _cursorIndexOfCurrentPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "current_position");
      final int _cursorIndexOfWatchDate = CursorUtil.getColumnIndexOrThrow(_cursor, "watch_date");
      final WatchHistoryEntity _result;
      if(_cursor.moveToFirst()) {
        _result = new WatchHistoryEntity();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _result.setId(_tmpId);
        final int _tmpUserID;
        _tmpUserID = _cursor.getInt(_cursorIndexOfUserID);
        _result.setUserID(_tmpUserID);
        final int _tmpItemID;
        _tmpItemID = _cursor.getInt(_cursorIndexOfItemID);
        _result.setItemID(_tmpItemID);
        final String _tmpItemType;
        _tmpItemType = _cursor.getString(_cursorIndexOfItemType);
        _result.setItemType(_tmpItemType);
        final int _tmpEpisodeID;
        _tmpEpisodeID = _cursor.getInt(_cursorIndexOfEpisodeID);
        _result.setEpisodeID(_tmpEpisodeID);
        final long _tmpCurrentPosition;
        _tmpCurrentPosition = _cursor.getLong(_cursorIndexOfCurrentPosition);
        _result.setCurrentPosition(_tmpCurrentPosition);
        final long _tmpWatchDate;
        _tmpWatchDate = _cursor.getLong(_cursorIndexOfWatchDate);
        _result.setWatchDate(_tmpWatchDate);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
