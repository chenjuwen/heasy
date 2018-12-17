package com.heasy.app.core.datastorage;

import android.content.ContentValues;
import android.content.Context;

import com.heasy.app.core.configuration.ConfigBean;
import com.heasy.app.core.utils.StringUtil;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/30.
 */
public class SQLCipherManagerImpl implements SQLCipherManager {
    private static Logger logger = LoggerFactory.getLogger(SQLCipherManagerImpl.class);
    private ConfigBean config;

    private SQLCipherManagerImpl(Context context, ConfigBean config){
        this.config = config;

        //so库加载
        SQLiteDatabase.loadLibs(context);
    }

    /**
     *  范例代码：
     *      sqlCipherManager = SQLCipherManagerImpl.getInstance(getApplicationContext(), new SQLCipherConfig());
     *
     * @param context
     * @param config
     * @return
     */
    public static SQLCipherManager getInstance(Context context, ConfigBean config) {
        SQLCipherManager instance = new SQLCipherManagerImpl(context, config);
        return instance;
    }

    private SQLiteDatabase openDatabase(){
        return SQLiteDatabase.openDatabase(config.getDBFilePath(), config.getDbPassword(), null, SQLiteDatabase.OPEN_READWRITE);
    }

    /**
     * 范例代码：
     *      Map<String, Object> dataMap = new HashMap<>();
     *      dataMap.put("name", "矿泉水");
     *      dataMap.put("price", "3.5");
     *      dataMap.put("pic", "kqs.png");
     *      long id = sqlCipherManager.insert("goods_info", dataMap);
     *
     * @param tableName 表名
     * @param dataMap 数据集合
     * @return
     */
    @Override
    public long insert(String tableName, Map<String, Object> dataMap){
        ContentValues contentValues = new ContentValues();
        for (Iterator<String> it = dataMap.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            Object value = dataMap.get(key);

            if(value instanceof Long){
                contentValues.put(key, (Long)value);
            }else if(value instanceof Integer){
                contentValues.put(key, (Integer)value);
            }else if(value instanceof Short){
                contentValues.put(key, (Short)value);
            }else if(value instanceof Double){
                contentValues.put(key, (Double)value);
            }else if(value instanceof Float){
                contentValues.put(key, (Float)value);
            }else if(value instanceof Boolean){
                contentValues.put(key, (Boolean)value);
            }else if(value instanceof Byte){
                contentValues.put(key, (Byte)value);
            }else{
                contentValues.put(key, (String)value);
            }
        }

        long id = 0;
        SQLiteDatabase database = openDatabase();
        database.beginTransaction();
        try {
            id = database.insert(tableName, null, contentValues);

            if(id != -1) {
                database.setTransactionSuccessful();
            }else{
                throw new RuntimeException("insert failed");
            }
        }catch(Exception ex){
            logger.error("insert error: " + ex.toString());
        }finally {
            database.endTransaction();
            database.close();
        }
        return id;
    }

    /**
     * 批量添加记录
     *
     * 范例代码：
     *      List<Map<String, Object>> dataList3 = new ArrayList<>();
     *
     *      Map<String, Object> dataMap3_1 = new HashMap<>();
     *      dataMap3_1.put("name", "矿泉水3_1");
     *      dataMap3_1.put("price", "3.31");
     *      dataMap3_1.put("pic", "kqs3_1.png");
     *      dataList3.add(dataMap3_1);
     *
     *      Map<String, Object> dataMap3_2 = new HashMap<>();
     *      dataMap3_2.put("name", "矿泉水3_2");
     *      dataMap3_2.put("price", "3.32");
     *      dataMap3_2.put("pic", "kqs3_2.png");
     *      dataList3.add(dataMap3_2);
     *
     *      List<Long> idsList = sqlCipherManager.batchInsert("goods_info", dataList3);
     *
     * @param tableName
     * @param dataList
     * @return
     */
    @Override
    public List<Long> batchInsert(String tableName, List<Map<String, Object>> dataList) {
        if(dataList == null || dataList.size() == 0){
            return null;
        }

        List<Long> idList = new ArrayList<>();

        SQLiteDatabase database = openDatabase();
        database.beginTransaction();
        try {
            for(Map<String, Object> dataMap : dataList){
                ContentValues contentValues = new ContentValues();
                for (Iterator<String> it = dataMap.keySet().iterator(); it.hasNext(); ) {
                    String key = it.next();
                    Object value = dataMap.get(key);

                    if(value instanceof Long){
                        contentValues.put(key, (Long)value);
                    }else if(value instanceof Integer){
                        contentValues.put(key, (Integer)value);
                    }else if(value instanceof Short){
                        contentValues.put(key, (Short)value);
                    }else if(value instanceof Double){
                        contentValues.put(key, (Double)value);
                    }else if(value instanceof Float){
                        contentValues.put(key, (Float)value);
                    }else if(value instanceof Boolean){
                        contentValues.put(key, (Boolean)value);
                    }else if(value instanceof Byte){
                        contentValues.put(key, (Byte)value);
                    }else{
                        contentValues.put(key, (String)value);
                    }
                }

                long id = database.insert(tableName, null, contentValues);

                if(id == -1) {
                    throw new RuntimeException("insert failed");
                }else{
                    idList.add(new Long(id));
                }
            }

            database.setTransactionSuccessful();

        }catch(Exception ex){
            logger.error("insert error: " + ex.toString());
        }finally {
            database.endTransaction();
            database.close();
        }

        return idList;
    }

    /**
     * 范例代码：
     *      sqlCipherManager.delete("goods_info", "id=?", new String[]{String.valueOf(id)});
     *
     * @param tableName
     * @param whereClause
     * @param whereArgs
     */
    @Override
    public void delete(String tableName, String whereClause, String[] whereArgs){
        SQLiteDatabase database = openDatabase();
        database.beginTransaction();
        try {
            database.delete(tableName, whereClause, whereArgs);
            database.setTransactionSuccessful();
        }catch(Exception ex){
            logger.error("delete error: " + ex.toString());
        }finally {
            database.endTransaction();
            database.close();
        }
    }

    /**
     * 范例代码：
     *      sqlCipherManager.clearTable("goods_info");
     *
     * @param tableName
     */
    @Override
    public void clearTable(String tableName){
        String sql = "delete from " + tableName;
        SQLiteDatabase database = openDatabase();
        database.beginTransaction();
        try {
            database.execSQL(sql);
            database.setTransactionSuccessful();
        }catch(Exception ex){
            logger.error("clearTable error: " + ex.toString());
        }finally {
            database.endTransaction();
            database.close();
        }
    }

    /**
     * 范例代码：
     *      Map<String, Object> dataMap2 = new HashMap<>();
     *      dataMap2.put("name", "矿泉水2");
     *      dataMap2.put("price", "4.5");
     *      dataMap2.put("pic", "kqs2.png");
     *      sqlCipherManager.update("goods_info", dataMap2, "id=?", new String[]{String.valueOf(id)});
     *
     * @param tableName
     * @param dataMap
     * @param whereClause
     * @param whereArgs
     */
    @Override
    public void update(String tableName, Map<String, Object> dataMap, String whereClause, String[] whereArgs){
        ContentValues contentValues = new ContentValues();
        for (Iterator<String> it = dataMap.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            Object value = dataMap.get(key);

            if(value instanceof Long){
                contentValues.put(key, (Long)value);
            }else if(value instanceof Integer){
                contentValues.put(key, (Integer)value);
            }else if(value instanceof Short){
                contentValues.put(key, (Short)value);
            }else if(value instanceof Double){
                contentValues.put(key, (Double)value);
            }else if(value instanceof Float){
                contentValues.put(key, (Float)value);
            }else if(value instanceof Boolean){
                contentValues.put(key, (Boolean)value);
            }else if(value instanceof Byte){
                contentValues.put(key, (Byte)value);
            }else{
                contentValues.put(key, (String)value);
            }
        }

        SQLiteDatabase database = openDatabase();
        database.beginTransaction();
        try {
            database.update(tableName, contentValues, whereClause, whereArgs);
            database.setTransactionSuccessful();
        }catch(Exception ex){
            logger.error("update error: " + ex.toString());
        }finally {
            database.endTransaction();
            database.close();
        }
    }

    /**
     * 范例代码：
     *      List<Map<String, String>> dataList = sqlCipherManager.query("goods_info", null, "id!=?", new String[]{String.valueOf(id)}, null);
     *
     * @param tableName 表名
     * @param columns   返回结果集需要包含的字段
     * @param selection 数据过滤条件，包含占位符(?)
     * @param selectionArgs 数据过滤条件的占位符的值
     * @param orderBy 排序
     * @return
     */
    @Override
    public List<Map<String, String>> query(String tableName, String[] columns, String selection, String[] selectionArgs, String orderBy){
        SQLiteDatabase database = openDatabase();
        Cursor cursor = null;
        List<Map<String, String>> dataList = new ArrayList<>();
        try {
            cursor = database.query(tableName, columns, selection, selectionArgs, null, null, orderBy);

            if(columns == null){
                columns = cursor.getColumnNames();
            }

            while (cursor.moveToNext()) {
                Map<String, String> rowData = new HashMap<>();
                for(String columnName : columns){
                    rowData.put(columnName, cursor.getString(cursor.getColumnIndex(columnName)));
                }
                dataList.add(rowData);
            }

        } catch (SQLException ex) {
            logger.error("query error: " + ex.toString());
        }finally {
            if (cursor != null) {
                cursor.close();
            }
            database.close();
        }
        return dataList;
    }

    /**
     * 范例代码：
     *      List<Map<String, String>> dataList2 = sqlCipherManager.rawQuery("select * from goods_info where id!=?", new String[]{String.valueOf(id)});
     *
     * @param sql   查询脚本语句，包含占位符(?)
     * @param selectionArgs 查询语句中占位符的值
     * @return
     */
    @Override
    public List<Map<String, String>> rawQuery(String sql, String[] selectionArgs){
        SQLiteDatabase database = openDatabase();
        Cursor cursor = null;
        List<Map<String, String>> dataList = new ArrayList<>();
        try {
            cursor = database.rawQuery(sql, selectionArgs);
            while (cursor.moveToNext()) {
                Map<String, String> rowData = new HashMap<>();
                for(String columnName : cursor.getColumnNames()){
                    rowData.put(columnName, cursor.getString(cursor.getColumnIndex(columnName)));
                }
                dataList.add(rowData);
            }

        } catch (SQLException ex) {
            logger.error("rawQuery error: " + ex.toString());
        }finally {
            if (cursor != null) {
                cursor.close();
            }
            database.close();
        }
        return dataList;
    }

    /**
     *      long total = sqlCipherManager.queryCount("goods_info", null, null);
     *      long total = sqlCipherManager.queryCount("goods_info", "status=1", null);
     *      long total = sqlCipherManager.queryCount("goods_info", "status=?", new String[]{"1"});
     *
     * @param tableName 表名
     * @param selection 数据过滤条件，包含占位符(?)
     * @param selectionArgs 数据过滤条件的占位符的值
     * @return
     */
    @Override
    public long queryCount(String tableName, String selection, String[] selectionArgs) {
        long total = 0;
        SQLiteDatabase database = openDatabase();
        Cursor cursor = null;
        try {
            String sql = "select count(*) from " + tableName;
            if(StringUtil.isNotEmpty(selection)){
                sql += " where " + selection;
            }

            cursor = database.rawQuery(sql, selectionArgs);
            if (cursor.moveToNext()) {
                total = cursor.getLong(0);
            }

        } catch (SQLException ex) {
            logger.error("queryCount error: " + ex.toString());
        }finally {
            if (cursor != null) {
                cursor.close();
            }
            database.close();
        }
        return total;
    }

    /**
     *  分页查询数据
     *
     *  范例代码：
     *      PageInfo<Map<String, String>> pageInfo = sqlCipherManager.queryForPage("goods_info", null, "status=?", new String[]{"1"}, "id asc", 1, 5);
     *
     * @param tableName 表名
     * @param columns   返回结果集需要包含的字段
     * @param selection 数据过滤条件，包含占位符(?)
     * @param selectionArgs 数据过滤条件的占位符的值
     * @param orderBy 排序
     * @param pageNum 当前页号
     * @param pageSize 每页记录数
     * @return
     */
    @Override
    public PageInfo queryForPage(String tableName, String[] columns, String selection, String[] selectionArgs, String orderBy, int pageNum, int pageSize) {
        List<Map<String, String>> dataList = new ArrayList<>();
        long total = 0;
        PageInfo<Map<String, String>> pageInfo = new PageInfo<>();

        Cursor cursor = null;
        SQLiteDatabase database = openDatabase();
        try {
            if(pageNum < 1){
                pageNum = 1;
            }

            if(pageSize < 1){
                pageSize = 1;
            }

            pageInfo.setPageNum(pageNum);
            pageInfo.setPageSize(pageSize);

            //总记录数
            total = queryCount(tableName, selection, selectionArgs);
            pageInfo.setTotal(total);

            if(total> 0 && pageNum <= pageInfo.getPages()) {
                //分页查询
                String limit = ((pageNum - 1) * pageSize) + "," + pageSize;

                cursor = database.query(tableName, columns, selection, selectionArgs, null, null, orderBy, limit);

                if (columns == null) {
                    columns = cursor.getColumnNames();
                }

                while (cursor.moveToNext()) {
                    Map<String, String> rowData = new HashMap<>();
                    for (String columnName : columns) {
                        rowData.put(columnName, cursor.getString(cursor.getColumnIndex(columnName)));
                    }
                    dataList.add(rowData);
                }

                pageInfo.setList(dataList);
            }else{
                pageInfo.setList(null);
                pageInfo.setTotal(0);
            }

        } catch (SQLException ex) {
            logger.error("query error: " + ex.toString());
        }finally {
            if (cursor != null) {
                cursor.close();
            }
            database.close();
        }

        return pageInfo;
    }

    /**
     * 范例代码：
     *      Map<String, String> map = sqlCipherManager.get("goods_info", "id", String.valueOf(id));
     *
     * @param tableName
     * @param idField
     * @param idValue
     * @return
     */
    @Override
    public Map<String, String> get(String tableName, String idField, String idValue) {
        return get(tableName, idField, idValue, "");
    }

    /**
     * 代码范例：
     *    Map<String, String> dataMap = cipherManager.get("course_category", "id", id, "id,subject,name,parent_id");
     *
     * @param tableName
     * @param idField
     * @param idValue
     * @param selectFields
     * @return
     */
    @Override
    public Map<String, String> get(String tableName, String idField, String idValue, String selectFields) {
        if(StringUtil.isEmpty(selectFields)){
            selectFields = "*";
        }

        List<Map<String, String>> dataList = rawQuery("select " + selectFields + " from " + tableName + " where " + idField + "=?", new String[]{idValue});
        if(dataList != null && dataList.size() > 0){
            return dataList.get(0);
        }

        return null;
    }

    /**
     * 范例代码：
     *      sqlCipherManager.executeSQL("delete from goods_info where id=" + id);
     *
     * @param sql
     */
    @Override
    public void executeSQL(String sql){
        SQLiteDatabase database = openDatabase();
        database.beginTransaction();
        try {
            database.execSQL(sql);
            database.setTransactionSuccessful();
        }catch(Exception ex){
            logger.error("executeSQL error: " + ex.toString());
        }finally {
            database.endTransaction();
            database.close();
        }
    }

    @Override
    public void executeSQL(List<String> sqlList) {
        SQLiteDatabase database = openDatabase();
        database.beginTransaction();
        try {
            for(String sql : sqlList) {
                database.execSQL(sql);
            }
            database.setTransactionSuccessful();
        }catch(Exception ex){
            logger.error("executeSQL error: " + ex.toString());
        }finally {
            database.endTransaction();
            database.close();
        }
    }

    /**
     * 范例代码：
     *      sqlCipherManager.executeSQL("delete from goods_info where id=?", new String[]{String.valueOf(id)});
     *
     * @param sql
     * @param bindArgs
     */
    @Override
    public void executeSQL(String sql, Object[] bindArgs){
        SQLiteDatabase database = openDatabase();
        database.beginTransaction();
        try {
            database.execSQL(sql, bindArgs);
            database.setTransactionSuccessful();
        }catch(Exception ex){
            logger.error("executeSQL error: " + ex.toString());
        }finally {
            database.endTransaction();
            database.close();
        }
    }

    /**
     * 加密数据库
     *
     * 范例代码：
     *      sqlCipherManager.encryptDB("vender.db", "en_vender.db", "123456");
     *
     * @param decryptFileName 加密前的数据库文件
     * @param encryptFileName 加密后的数据库文件
     * @param key 秘钥
     */
    @Override
    public void encryptDB(final String decryptFileName, final String encryptFileName, final String key){
        new Thread(){
            @Override
            public void run() {
                try {
                    //加密前
                    File originalFile = new File(config.getSdcardRootPath() + decryptFileName);
                    SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(originalFile, "", null);
                    logger.debug("database version: " + database.getVersion());

                    //删除旧文件
                    File tmpFile = new File(config.getSdcardRootPath() + encryptFileName);
                    if (tmpFile.exists() && tmpFile.isFile()) {
                        tmpFile.delete();
                    }

                    //加密后
                    //File destFile = dbHelper.getContext().getDatabasePath(dbHelper.getConfig().getDbBasePath() + encryptFileName);
                    File destFile = new File(config.getSdcardRootPath() + encryptFileName);
                    logger.debug(destFile.getAbsolutePath());

                    //连接到加密后的数据库，并设置密码
                    database.rawExecSQL(String.format("ATTACH DATABASE '%s' as " + encryptFileName.split("\\.")[0] + " KEY '" + key + "';", destFile.getAbsolutePath()));

                    //输出要加密的数据库表和数据到加密后的数据库文件中
                    database.rawExecSQL("SELECT sqlcipher_export('" + encryptFileName.split("\\.")[0] + "');");

                    //断开同加密后的数据库的连接
                    database.rawExecSQL("DETACH DATABASE " + encryptFileName.split("\\.")[0] + ";");

                    //打开加密后的数据库
                    SQLiteDatabase encrypteddatabase = SQLiteDatabase.openOrCreateDatabase(destFile, key, null);
                    encrypteddatabase.setVersion(database.getVersion());
                    encrypteddatabase.close();//关闭数据库

                    database.close();
                    logger.debug("ok");

                }catch (Exception ex){
                    logger.error("encryptDB error: " + ex.toString());
                }
            }
        }.start();
    }

    /**
     * 解密数据库
     *
     * 范例代码：
     *      sqlCipherManager.decryptDB("en_vender.db", "de_vender.db", "123456");
     *
     * @param encryptFileName 解密前的数据库文件
     * @param decryptFileName 解密后的数据库文件
     * @param key 秘钥
     */
    @Override
    public void decryptDB(final String encryptFileName, final String decryptFileName, final String key){
        new Thread(){
            @Override
            public void run() {
                try{
                    //解密前
                    //File databaseFile = dbHelper.getContext().getDatabasePath(dbHelper.getConfig().getDbBasePath() + encryptFileName);
                    File databaseFile = new File(config.getSdcardRootPath() + encryptFileName);
                    SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, key, null);
                    logger.debug("database version: " + database.getVersion());

                    //删除旧文件
                    File tmpFile = new File(config.getSdcardRootPath() + decryptFileName);
                    if(tmpFile.exists() && tmpFile.isFile()){
                        tmpFile.delete();
                    }

                    //解密后
                   // File destFile = dbHelper.getContext().getDatabasePath(dbHelper.getConfig().getDbBasePath() + decryptFileName);
                    File destFile = new File(config.getSdcardRootPath() + decryptFileName);
                    logger.debug(destFile.getAbsolutePath());

                    //连接到解密后的数据库，并设置密码为空
                    database.rawExecSQL(String.format("ATTACH DATABASE '%s' as "+ decryptFileName.split("\\.")[0] +" KEY '';", destFile.getAbsolutePath()));
                    database.rawExecSQL("SELECT sqlcipher_export('"+ decryptFileName.split("\\.")[0] +"');");
                    database.rawExecSQL("DETACH DATABASE "+ decryptFileName.split("\\.")[0] +";");

                    SQLiteDatabase decryptDatabase = SQLiteDatabase.openOrCreateDatabase(destFile, "", null);
                    decryptDatabase.setVersion(database.getVersion());
                    decryptDatabase.close();

                    database.close();
                    logger.debug("ok");

                }catch (Exception ex){
                    logger.error("decryptDB error: " + ex.toString());
                }
            }
        }.start();
    }

}
