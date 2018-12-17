package com.heasy.app.core.datastorage;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/2/6.
 */
public interface SQLCipherManager {
    long insert(String tableName, Map<String, Object> dataMap);

    List<Long> batchInsert(String tableName, List<Map<String, Object>> dataList);

    void delete(String tableName, String whereClause, String[] whereArgs);

    void clearTable(String tableName);

    void update(String tableName, Map<String, Object> dataMap, String whereClause, String[] whereArgs);

    List<Map<String, String>> query(String tableName, String[] columns, String selection, String[] selectionArgs, String orderBy);

    List<Map<String, String>> rawQuery(String sql, String[] selectionArgs);

    long queryCount(String tableName, String selection, String[] selectionArgs);

    PageInfo queryForPage(String tableName, String[] columns, String selection, String[] selectionArgs, String orderBy, int pageNum, int pageSize);

    Map<String, String> get(String tableName, String idField, String idValue);

    Map<String, String> get(String tableName, String idField, String idValue, String selectFields);

    void executeSQL(String sql);

    void executeSQL(List<String> sqlList);

    void executeSQL(String sql, Object[] bindArgs);

    void encryptDB(String decryptFileName, String encryptFileName, String key);

    void decryptDB(String encryptFileName, String decryptFileName, String key);
}
