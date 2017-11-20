package com.gmail.fomichov.m.drillingmagazine.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.gmail.fomichov.m.drillingmagazine.utils.MyLog;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * https://github.com/jgilfelt/android-sqlite-asset-helper
 * грузим базу данных из папки
 */

public class ObjectDB extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "db_objects.db";
    private static final int DATABASE_VERSION = 1;

    public ObjectDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // запрос на получение списка обьектов
    public Cursor getObject() {
        SQLiteDatabase database = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(dbTab.TAB_OBJECT);
        Cursor cursor = queryBuilder.query(database, null, null, null, null, null, null);
        return cursor;
    }

    // запрос на получение списка выработок
    public Cursor getExcavation() {
        SQLiteDatabase database = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(dbTab.TAB_EXCAVATION);
        Cursor cursor = queryBuilder.query(database, null, null, null, null, null, excColumn.ID_EXCAVATION + " ASC");
        return cursor;
    }

    // запрос на получение списка выработок по обьекту
    public Cursor getExcavation(String nameObject) {
        SQLiteDatabase database = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(dbTab.TAB_EXCAVATION);
        String queryData = excColumn.NAME_OBJECT + " = ? ";
        Cursor cursor = queryBuilder.query(database, null, queryData, new String[]{nameObject}, null, null, null);
        return cursor;
    }

    // запрос на получение списка выработок по обьекту и типу выработки
    public Cursor getExcavation(String nameObject, String typeExc) {
        SQLiteDatabase database = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(dbTab.TAB_EXCAVATION);
        String queryData = excColumn.NAME_OBJECT + " = ? AND " + excColumn.TYPE_EXCAVATION + " = ?";
        Cursor cursor = queryBuilder.query(database, null, queryData, new String[]{nameObject, typeExc}, null, null, null);
        return cursor;
    }

//    public Cursor getProbe(String nameObject, String nameExc) {
//        SQLiteDatabase database = getReadableDatabase();
//        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
//        queryBuilder.setTables(dbTab.TAB_PROBE);
//        String queryData = probeColumn.OBJECT_NAME + " = ? AND " + probeColumn.EXCAVATION_NAME + " = ?";
//        Cursor cursor = queryBuilder.query(database, null, queryData, new String[]{nameObject, nameExc}, null, null, probeColumn.DEPTH_PROBE + " ASC"); // сортировка по глубине
//        return cursor;
//    }

    public Cursor getProbe(long keyExc) {
        SQLiteDatabase database = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(dbTab.TAB_PROBE);
        String queryData = probeColumn.ID_EXC_PROBE + " = ? ";
        Cursor cursor = queryBuilder.query(database, null, queryData, new String[]{String.valueOf(keyExc)}, null, null, probeColumn.DEPTH_PROBE + " ASC"); // сортировка по глубине
        return cursor;
    }

//    public Cursor getLayer(String nameObject, String nameExc) {
//        SQLiteDatabase database = getReadableDatabase();
//        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
//        queryBuilder.setTables(dbTab.TAB_LAYER);
//        String queryData = layerColumn.OBJECT_NAME + " = ? AND " + layerColumn.EXCAVATION_NAME + " = ?";
//        Cursor cursor = queryBuilder.query(database, null, queryData, new String[]{nameObject, nameExc}, null, null, layerColumn.START_LAYER + " ASC"); // сортировка по глубине
//        return cursor;
//    }

    public Cursor getLayer(long idExc) {
        SQLiteDatabase database = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(dbTab.TAB_LAYER);
        String queryData = layerColumn.ID_LAYER_EXC + " = ? ";
        Cursor cursor = queryBuilder.query(database, null, queryData, new String[]{String.valueOf(idExc)}, null, null, layerColumn.START_LAYER + " ASC"); // сортировка по глубине
        return cursor;
    }

    public Cursor getLayerMaxDepth(long idExc) {
        SQLiteDatabase database = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(dbTab.TAB_LAYER);
        String queryData = layerColumn.ID_LAYER_EXC + " = ? " ;
        Cursor cursor = queryBuilder.query(database, null, queryData, new String[]{String.valueOf(idExc)}, null, null, layerColumn.END_LAYER + " ASC"); // сортировка по глубине
        return cursor;
    }

    public interface dbTab {
        String TAB_OBJECT = "tab_object";
        String TAB_EXCAVATION = "tab_excavation";
        String TAB_LAYER = "tab_layer";
        String TAB_PROBE = "tab_probe";
        String TAB_ZOND = "tab_zond";
    }

    public interface objColumn {
        String NAME = "name";
        String DESCRIPTION = "description";
        String ID_OBJECT = "_id";
    }

    public interface excColumn {
        String ID_EXCAVATION = "_id";
        String NAME_OBJECT = "fk_tab_object_name_excavation";
        String NAME_EXCAVATION = "name_excavation";
        String DESCRIPTION_EXCAVATION = "description_excavation";
        String ABSOLUTE_ELEVATION = "absolute_elevation";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String DATE_START = "date_start";
        String DATE_END = "date_end";
        String PENETRATION = "penetration_method";
        String WHO_WORKED = "who_worked";
        String EQUIPMENT = "equipment";
        String TYPE_EXCAVATION = "type_excavation";
        String WATER_SHOW = "water_show";
        String WATER_STAY = "water_stay";
        String DATE_WATER_STAY = "date_water_stay";
        String DATE_WATER_SHOW = "date_water_show";
        String DEPTH_EXCAVATION = "depth_excavation";
    }

    public interface layerColumn {
        String OBJECT_NAME = "fk_tab_object_name_layer";
        String START_LAYER = "start_layer";
        String END_LAYER = "end_layer";
        String DESCRIPTION_LAYER = "description_layer";
        String DATE_LAYER = "date_layer";
        String ID_LAYER_EXC = "fk_tab_excavation_id_layer";
        String ID_LAYER = "_id";
    }

    public interface probeColumn {
        String OBJECT_NAME = "fk_tab_object_name_probe";
        String DATE_PROBE = "date_probe";
        String TYPE_PROBE = "type_probe";
        String DEPTH_PROBE = "depth_probe";
        String ID_EXC_PROBE = "fk_tab_excavation_id_probe";
        String PROBE_DESCRIPTION = "probe_description";
        String ID_PROBE = "_id";
    }

    public interface zondColumn {
        String ID_ZOND = "_id";
        String DEPTH_ZOND = "depth_zond";
        String Q_RESIST = "q_resist";
        String F_RESIST = "f_resist";
        String OBJECT_NAME = "fk_tab_object_name_zond";
        String ID_EXC_ZOND = "fk_tab_excavation_id_zond";
    }
}
