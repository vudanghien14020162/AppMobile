package com.mobitv.ott.database.dao;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.mobitv.ott.database.entity.SearchHistoryEntity;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class SearchHistoryDao_Impl implements SearchHistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SearchHistoryEntity> __insertionAdapterOfSearchHistoryEntity;

  private final EntityDeletionOrUpdateAdapter<SearchHistoryEntity> __deletionAdapterOfSearchHistoryEntity;

  private final EntityDeletionOrUpdateAdapter<SearchHistoryEntity> __updateAdapterOfSearchHistoryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public SearchHistoryDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSearchHistoryEntity = new EntityInsertionAdapter<SearchHistoryEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `tbl_search_history` (`id`,`user_id`,`keyword`,`keyword_khongdau`,`search_date`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, SearchHistoryEntity value) {
        stmt.bindLong(1, value.getId());
        stmt.bindLong(2, value.getUserID());
        if (value.getKeyword() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getKeyword());
        }
        if (value.getKeywordKhongDau() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getKeywordKhongDau());
        }
        stmt.bindLong(5, value.getSearchDate());
      }
    };
    this.__deletionAdapterOfSearchHistoryEntity = new EntityDeletionOrUpdateAdapter<SearchHistoryEntity>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `tbl_search_history` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, SearchHistoryEntity value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__updateAdapterOfSearchHistoryEntity = new EntityDeletionOrUpdateAdapter<SearchHistoryEntity>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `tbl_search_history` SET `id` = ?,`user_id` = ?,`keyword` = ?,`keyword_khongdau` = ?,`search_date` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, SearchHistoryEntity value) {
        stmt.bindLong(1, value.getId());
        stmt.bindLong(2, value.getUserID());
        if (value.getKeyword() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getKeyword());
        }
        if (value.getKeywordKhongDau() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getKeywordKhongDau());
        }
        stmt.bindLong(5, value.getSearchDate());
        stmt.bindLong(6, value.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM tbl_search_history";
        return _query;
      }
    };
  }

  @Override
  public void insert(final SearchHistoryEntity searchHistoryEntity) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfSearchHistoryEntity.insert(searchHistoryEntity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteSearchHistory(final SearchHistoryEntity searchHistoryEntity) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfSearchHistoryEntity.handle(searchHistoryEntity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final SearchHistoryEntity searchHistoryEntity) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfSearchHistoryEntity.handle(searchHistoryEntity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAll() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAll.release(_stmt);
    }
  }

  @Override
  public List<SearchHistoryEntity> loadAllSearch() {
    final String _sql = "SELECT * FROM tbl_search_history  ORDER BY search_date DESC LIMIT 5";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfUserID = CursorUtil.getColumnIndexOrThrow(_cursor, "user_id");
      final int _cursorIndexOfKeyword = CursorUtil.getColumnIndexOrThrow(_cursor, "keyword");
      final int _cursorIndexOfKeywordKhongDau = CursorUtil.getColumnIndexOrThrow(_cursor, "keyword_khongdau");
      final int _cursorIndexOfSearchDate = CursorUtil.getColumnIndexOrThrow(_cursor, "search_date");
      final List<SearchHistoryEntity> _result = new ArrayList<SearchHistoryEntity>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final SearchHistoryEntity _item;
        _item = new SearchHistoryEntity();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final int _tmpUserID;
        _tmpUserID = _cursor.getInt(_cursorIndexOfUserID);
        _item.setUserID(_tmpUserID);
        final String _tmpKeyword;
        _tmpKeyword = _cursor.getString(_cursorIndexOfKeyword);
        _item.setKeyword(_tmpKeyword);
        final String _tmpKeywordKhongDau;
        _tmpKeywordKhongDau = _cursor.getString(_cursorIndexOfKeywordKhongDau);
        _item.setKeywordKhongDau(_tmpKeywordKhongDau);
        final long _tmpSearchDate;
        _tmpSearchDate = _cursor.getLong(_cursorIndexOfSearchDate);
        _item.setSearchDate(_tmpSearchDate);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public SearchHistoryEntity loadSearchByKeyword(final String keywordKhongDau) {
    final String _sql = "SELECT * FROM tbl_search_history WHERE keyword_khongdau = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (keywordKhongDau == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, keywordKhongDau);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfUserID = CursorUtil.getColumnIndexOrThrow(_cursor, "user_id");
      final int _cursorIndexOfKeyword = CursorUtil.getColumnIndexOrThrow(_cursor, "keyword");
      final int _cursorIndexOfKeywordKhongDau = CursorUtil.getColumnIndexOrThrow(_cursor, "keyword_khongdau");
      final int _cursorIndexOfSearchDate = CursorUtil.getColumnIndexOrThrow(_cursor, "search_date");
      final SearchHistoryEntity _result;
      if(_cursor.moveToFirst()) {
        _result = new SearchHistoryEntity();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _result.setId(_tmpId);
        final int _tmpUserID;
        _tmpUserID = _cursor.getInt(_cursorIndexOfUserID);
        _result.setUserID(_tmpUserID);
        final String _tmpKeyword;
        _tmpKeyword = _cursor.getString(_cursorIndexOfKeyword);
        _result.setKeyword(_tmpKeyword);
        final String _tmpKeywordKhongDau;
        _tmpKeywordKhongDau = _cursor.getString(_cursorIndexOfKeywordKhongDau);
        _result.setKeywordKhongDau(_tmpKeywordKhongDau);
        final long _tmpSearchDate;
        _tmpSearchDate = _cursor.getLong(_cursorIndexOfSearchDate);
        _result.setSearchDate(_tmpSearchDate);
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
