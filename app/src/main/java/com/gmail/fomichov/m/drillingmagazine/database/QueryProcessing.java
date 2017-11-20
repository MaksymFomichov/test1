package com.gmail.fomichov.m.drillingmagazine.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gmail.fomichov.m.drillingmagazine.geology.Excavation;
import com.gmail.fomichov.m.drillingmagazine.geology.Layer;
import com.gmail.fomichov.m.drillingmagazine.geology.ObjectGeology;
import com.gmail.fomichov.m.drillingmagazine.geology.Probe;

import java.util.ArrayList;
import java.util.List;

public class QueryProcessing {

    /*
    запросы объектов
     */

    // получаем обьекты
    public static List<ObjectGeology> getListObjects(Context context) {
        List<ObjectGeology> list = new ArrayList<>();
        ObjectDB db = new ObjectDB(context);
        Cursor cursor = db.getObject();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.objColumn.NAME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.objColumn.DESCRIPTION));
            list.add(new ObjectGeology(name, description));
        }
        cursor.close();
        db.close();
        return list;
    }

    // получаем обьекты
    public static List<ObjectGeology> getNameObjects(Context context) {
        List<ObjectGeology> list = new ArrayList<>();
        ObjectDB db = new ObjectDB(context);
        Cursor cursor = db.getObject();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.objColumn.NAME));
            list.add(new ObjectGeology(name));
        }
        cursor.close();
        db.close();
        return list;
    }

    // перезаписываем данные обьекта
    public static void editObject(String oldName, String newName, String description, Context context) {
        ContentValues cv = new ContentValues();
        ObjectDB objectDB = new ObjectDB(context);
        SQLiteDatabase db = objectDB.getWritableDatabase();
        cv.put(ObjectDB.objColumn.NAME, newName);
        cv.put(ObjectDB.objColumn.DESCRIPTION, description);
        db.setForeignKeyConstraintsEnabled(true);
        db.update(ObjectDB.dbTab.TAB_OBJECT, cv, ObjectDB.objColumn.NAME + " = ?", new String[]{oldName});
        db.close();
    }

    // добавляем новый обьект
    public static void addNewObject(String name, String description, Context context) {
        ContentValues cv = new ContentValues();
        ObjectDB objectDB = new ObjectDB(context);
        SQLiteDatabase db = objectDB.getWritableDatabase();
        cv.put(ObjectDB.objColumn.NAME, name);
        cv.put(ObjectDB.objColumn.DESCRIPTION, description);
        db.insert(ObjectDB.dbTab.TAB_OBJECT, null, cv);
        db.close();
    }

    // удаляем обьект
    public static void deleteObject(String name, Context context) {
        ObjectDB objectDB = new ObjectDB(context);
        SQLiteDatabase db = objectDB.getWritableDatabase();
        db.setForeignKeyConstraintsEnabled(true);
        db.delete(ObjectDB.dbTab.TAB_OBJECT, ObjectDB.objColumn.NAME + " = ?", new String[]{name});
        db.close();
    }

    /*
    запросы выработок
     */

    // получаем id самой новой выработки, нужен для загрузки слоев и проб выработки из файла
    public static long getNewExcavationId(Context context) {
        ObjectDB db = new ObjectDB(context);
        Cursor cursor = db.getExcavation();
        long keyExc = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                keyExc = cursor.getLong(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.ID_EXCAVATION));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return keyExc;
    }

    // получаем id самого нового обьекта, нужен для загрузки выработок, слоев и проб выработки из файла
    public static long getNewObjectId(Context context) {
        ObjectDB db = new ObjectDB(context);
        Cursor cursor = db.getObject();
        long keyObj = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                keyObj = cursor.getLong(cursor.getColumnIndexOrThrow(ObjectDB.objColumn.ID_OBJECT));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return keyObj;
    }

    // получаем выработки по имени обьекта
    public static List<Excavation> getListExcavation(Context context, String nameObject) {
        List<Excavation> list = new ArrayList<>();
        ObjectDB db = new ObjectDB(context);
        Cursor cursor = db.getExcavation(nameObject);
        while (cursor.moveToNext()) {
            String object_excavation = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.NAME_OBJECT));
            String name_excavation = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.NAME_EXCAVATION));
            String description_excavation = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.DESCRIPTION_EXCAVATION));
            String latitude = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.LATITUDE));
            String longitude = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.LONGITUDE));
            String date_start = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.DATE_START));
            String date_end = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.DATE_END));
            String penetration_method = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.PENETRATION));
            String who_worked = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.WHO_WORKED));
            String equipment = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.EQUIPMENT));
            String typeExcavation = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.TYPE_EXCAVATION));
            String dateWaterStay = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.DATE_WATER_STAY));
            String dateWaterShow = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.DATE_WATER_SHOW));
            double absoluteElevation = cursor.getDouble(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.ABSOLUTE_ELEVATION));
            double waterShow = cursor.getDouble(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.WATER_SHOW));
            double waterStay = cursor.getDouble(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.WATER_STAY));
            long keyExc = cursor.getLong(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.ID_EXCAVATION));
            list.add(new Excavation(name_excavation, date_start, date_end, description_excavation, latitude, longitude, equipment, who_worked,
                    penetration_method, typeExcavation, object_excavation, absoluteElevation, keyExc, waterShow, waterStay, dateWaterStay, dateWaterShow));
        }
        cursor.close();
        db.close();
        return list;
    }

    // получаем выработки по имени обьекта и типу выработки для проверки в диалоге
    public static List<Excavation> getListExcavation(Context context, String nameObject, String typeExc) {
        List<Excavation> list = new ArrayList<>();
        ObjectDB db = new ObjectDB(context);
        Cursor cursor = db.getExcavation(nameObject, typeExc);
        while (cursor.moveToNext()) {
            String object_excavation = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.NAME_OBJECT));
            String name_excavation = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.NAME_EXCAVATION));
            String typeExcavation = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.TYPE_EXCAVATION));
            list.add(new Excavation(name_excavation, typeExcavation, object_excavation));
        }
        cursor.close();
        db.close();
        return list;
    }

    // получаем все выработки
    public static List<Excavation> getListExcavationAll(Context context) {
        List<Excavation> list = new ArrayList<>();
        ObjectDB db = new ObjectDB(context);
        Cursor cursor = db.getExcavation();
        while (cursor.moveToNext()) {
            String object_excavation = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.NAME_OBJECT));
            String name_excavation = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.NAME_EXCAVATION));
            String description_excavation = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.DESCRIPTION_EXCAVATION));
            String latitude = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.LATITUDE));
            String longitude = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.LONGITUDE));
            String date_start = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.DATE_START));
            String date_end = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.DATE_END));
            String penetration_method = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.PENETRATION));
            String who_worked = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.WHO_WORKED));
            String equipment = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.EQUIPMENT));
            String typeExcavation = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.TYPE_EXCAVATION));
            double absoluteElevation = cursor.getDouble(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.ABSOLUTE_ELEVATION));
            double waterShow = cursor.getDouble(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.WATER_SHOW));
            double waterStay = cursor.getDouble(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.WATER_STAY));
            String dateWaterStay = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.DATE_WATER_STAY));
            String dateWaterShow = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.DATE_WATER_SHOW));
            long keyExc = cursor.getLong(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.ID_EXCAVATION));
            list.add(new Excavation(name_excavation, date_start, date_end, description_excavation, latitude, longitude, equipment, who_worked,
                    penetration_method, typeExcavation, object_excavation, absoluteElevation, keyExc, waterShow, waterStay, dateWaterStay, dateWaterShow));
        }
        cursor.close();
        db.close();
        return list;
    }

    // получаем выработки обьекта
    public static List<Excavation> getListExcavation(Context context) {
        List<Excavation> list = new ArrayList<>();
        ObjectDB db = new ObjectDB(context);
        Cursor cursor = db.getExcavation();
        while (cursor.moveToNext()) {
            String object = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.excColumn.NAME_OBJECT));
            list.add(new Excavation(object));
        }
        cursor.close();
        db.close();
        return list;
    }

    // добавляем новую выработку
    public static void addNewExcavation(String object, String type, String number, String dateStart, String dateEnd, String dateWaterStay, String dateWaterShow, String latitude, String longitude,
                                        String description, String who, String than, String how, double showWater, double stayWater, double absoluteElevation, Context context) {
        ContentValues cv = new ContentValues();
        ObjectDB objectDB = new ObjectDB(context);
        SQLiteDatabase db = objectDB.getWritableDatabase();
        cv.put(ObjectDB.excColumn.NAME_OBJECT, object);
        cv.put(ObjectDB.excColumn.NAME_EXCAVATION, number);
        cv.put(ObjectDB.excColumn.TYPE_EXCAVATION, type);
        cv.put(ObjectDB.excColumn.DATE_START, dateStart);
        cv.put(ObjectDB.excColumn.DATE_END, dateEnd);
        cv.put(ObjectDB.excColumn.LATITUDE, latitude);
        cv.put(ObjectDB.excColumn.LONGITUDE, longitude);
        cv.put(ObjectDB.excColumn.DESCRIPTION_EXCAVATION, description);
        cv.put(ObjectDB.excColumn.WHO_WORKED, who);
        cv.put(ObjectDB.excColumn.EQUIPMENT, than);
        cv.put(ObjectDB.excColumn.PENETRATION, how);
        cv.put(ObjectDB.excColumn.ABSOLUTE_ELEVATION, absoluteElevation);
        cv.put(ObjectDB.excColumn.WATER_SHOW, showWater);
        cv.put(ObjectDB.excColumn.WATER_STAY, stayWater);
        cv.put(ObjectDB.excColumn.DATE_WATER_STAY, dateWaterStay);
        cv.put(ObjectDB.excColumn.DATE_WATER_SHOW, dateWaterShow);
        db.insert(ObjectDB.dbTab.TAB_EXCAVATION, null, cv);
        db.close();
    }

    // редактируем новую выработку
    public static void editExcavation(long idExc, String type, String number, String dateStart, String dateEnd, String dateWaterStay, String dateWaterShow, String latitude,
                                      String longitude, String description, String who, String than, String how, double showWater, double stayWater, double absoluteElevation, Context context) {
        ContentValues cv = new ContentValues();
        ObjectDB objectDB = new ObjectDB(context);
        SQLiteDatabase db = objectDB.getWritableDatabase();
        cv.put(ObjectDB.excColumn.NAME_EXCAVATION, number);
        cv.put(ObjectDB.excColumn.TYPE_EXCAVATION, type);
        cv.put(ObjectDB.excColumn.DATE_START, dateStart);
        cv.put(ObjectDB.excColumn.DATE_END, dateEnd);
        cv.put(ObjectDB.excColumn.LATITUDE, latitude);
        cv.put(ObjectDB.excColumn.LONGITUDE, longitude);
        cv.put(ObjectDB.excColumn.DESCRIPTION_EXCAVATION, description);
        cv.put(ObjectDB.excColumn.WHO_WORKED, who);
        cv.put(ObjectDB.excColumn.EQUIPMENT, than);
        cv.put(ObjectDB.excColumn.PENETRATION, how);
        cv.put(ObjectDB.excColumn.ABSOLUTE_ELEVATION, absoluteElevation);
        cv.put(ObjectDB.excColumn.WATER_SHOW, showWater);
        cv.put(ObjectDB.excColumn.WATER_STAY, stayWater);
        cv.put(ObjectDB.excColumn.DATE_WATER_STAY, dateWaterStay);
        cv.put(ObjectDB.excColumn.DATE_WATER_SHOW, dateWaterShow);
        db.update(ObjectDB.dbTab.TAB_EXCAVATION, cv, ObjectDB.excColumn.ID_EXCAVATION + " = ?", new String[]{String.valueOf(idExc)});
        db.close();
    }

    // удаляем выработку
    public static void deleteExcavation(long idExc, Context context) {
        ObjectDB objectDB = new ObjectDB(context);
        SQLiteDatabase db = objectDB.getWritableDatabase();
        db.setForeignKeyConstraintsEnabled(true);
        db.delete(ObjectDB.dbTab.TAB_EXCAVATION, ObjectDB.excColumn.ID_EXCAVATION + " = ?", new String[]{String.valueOf(idExc)});
        db.close();
    }

    /*
    запросы слоев
     */

    public static void deleteLayer(long idLayer, Context context) {
        ObjectDB objectDB = new ObjectDB(context);
        SQLiteDatabase db = objectDB.getWritableDatabase();
        db.setForeignKeyConstraintsEnabled(true);
        db.delete(ObjectDB.dbTab.TAB_LAYER, ObjectDB.layerColumn.ID_LAYER + " = ?", new String[]{String.valueOf(idLayer)});
        db.close();
    }

    public static void addNewLayer(String nameObj, double startDepth, double endDepth, String description, long idExc, Context context) {
        ContentValues cv = new ContentValues();
        ObjectDB objectDB = new ObjectDB(context);
        SQLiteDatabase db = objectDB.getWritableDatabase();
        cv.put(ObjectDB.layerColumn.OBJECT_NAME, nameObj);
        cv.put(ObjectDB.layerColumn.START_LAYER, startDepth);
        cv.put(ObjectDB.layerColumn.END_LAYER, endDepth);
        cv.put(ObjectDB.layerColumn.DESCRIPTION_LAYER, description);
        cv.put(ObjectDB.layerColumn.ID_LAYER_EXC, idExc);
        db.insert(ObjectDB.dbTab.TAB_LAYER, null, cv);
        db.close();
    }

    public static void editLayer(double startDepth, double endDepth, String description, long idLayer, Context context) {
        ContentValues cv = new ContentValues();
        ObjectDB objectDB = new ObjectDB(context);
        SQLiteDatabase db = objectDB.getWritableDatabase();
        cv.put(ObjectDB.layerColumn.START_LAYER, startDepth);
        cv.put(ObjectDB.layerColumn.END_LAYER, endDepth);
        cv.put(ObjectDB.layerColumn.DESCRIPTION_LAYER, description);
        db.update(ObjectDB.dbTab.TAB_LAYER, cv, ObjectDB.layerColumn.ID_LAYER + " = ?", new String[]{String.valueOf(idLayer)});
        db.close();
    }

    public static List<Layer> getListLayer(Context context, long idExc) {
        List<Layer> list = new ArrayList<>();
        ObjectDB db = new ObjectDB(context);
        Cursor cursor = db.getLayer(idExc);
        while (cursor.moveToNext()) {
            double startDepth = cursor.getDouble(cursor.getColumnIndexOrThrow(ObjectDB.layerColumn.START_LAYER));
            double endDepth = cursor.getDouble(cursor.getColumnIndexOrThrow(ObjectDB.layerColumn.END_LAYER));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.layerColumn.DESCRIPTION_LAYER));
            long idLayer = cursor.getLong(cursor.getColumnIndexOrThrow(ObjectDB.layerColumn.ID_LAYER));
            list.add(new Layer(startDepth, endDepth, description, idLayer));
        }
        cursor.close();
        db.close();
        return list;
    }

    public static List<Layer> getListLayerJSON(Context context, long keyExc) {
        List<Layer> list = new ArrayList<>();
        ObjectDB db = new ObjectDB(context);
        Cursor cursor = db.getLayer(keyExc);
        while (cursor.moveToNext()) {
            double startDepth = cursor.getDouble(cursor.getColumnIndexOrThrow(ObjectDB.layerColumn.START_LAYER));
            double endDepth = cursor.getDouble(cursor.getColumnIndexOrThrow(ObjectDB.layerColumn.END_LAYER));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.layerColumn.DESCRIPTION_LAYER));
            list.add(new Layer(startDepth, endDepth, description));
        }
        cursor.close();
        db.close();
        return list;
    }

    /*
    запросы проб
     */

    public static void addNewProbe(String nameObj, double depthProbe, String descriptionProbe, long keyExc, String typeProbe, Context context) {
        ContentValues cv = new ContentValues();
        ObjectDB objectDB = new ObjectDB(context);
        SQLiteDatabase db = objectDB.getWritableDatabase();
        cv.put(ObjectDB.probeColumn.OBJECT_NAME, nameObj);
        cv.put(ObjectDB.probeColumn.DEPTH_PROBE, depthProbe);
        cv.put(ObjectDB.probeColumn.PROBE_DESCRIPTION, descriptionProbe);
        cv.put(ObjectDB.probeColumn.ID_EXC_PROBE, keyExc);
        cv.put(ObjectDB.probeColumn.TYPE_PROBE, typeProbe);
        db.insert(ObjectDB.dbTab.TAB_PROBE, null, cv);
        db.close();
    }

    public static void editProbe(long idProbe, double depthProbe, String descriptionProbe, String typeProbe, Context context) {
        ContentValues cv = new ContentValues();
        ObjectDB objectDB = new ObjectDB(context);
        SQLiteDatabase db = objectDB.getWritableDatabase();
        cv.put(ObjectDB.probeColumn.DEPTH_PROBE, depthProbe);
        cv.put(ObjectDB.probeColumn.PROBE_DESCRIPTION, descriptionProbe);
        cv.put(ObjectDB.probeColumn.TYPE_PROBE, typeProbe);
        db.update(ObjectDB.dbTab.TAB_PROBE, cv, ObjectDB.probeColumn.ID_PROBE + " = ?", new String[]{String.valueOf(idProbe)});
        db.close();
    }

    public static List<Probe> getListProbe(Context context, long keyExc) {
        List<Probe> list = new ArrayList<>();
        ObjectDB db = new ObjectDB(context);
        Cursor cursor = db.getProbe(keyExc);
        while (cursor.moveToNext()) {
            double depth = cursor.getDouble(cursor.getColumnIndexOrThrow(ObjectDB.probeColumn.DEPTH_PROBE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.probeColumn.PROBE_DESCRIPTION));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.probeColumn.TYPE_PROBE));
            long idProbe = cursor.getLong(cursor.getColumnIndexOrThrow(ObjectDB.probeColumn.ID_PROBE));
            list.add(new Probe(depth, description, type, idProbe));
        }
        cursor.close();
        db.close();
        return list;
    }

    public static List<Probe> getListProbeJSON(Context context, long keyExc) {
        List<Probe> list = new ArrayList<>();
        ObjectDB db = new ObjectDB(context);
        Cursor cursor = db.getProbe(keyExc);
        while (cursor.moveToNext()) {
            double depth = cursor.getDouble(cursor.getColumnIndexOrThrow(ObjectDB.probeColumn.DEPTH_PROBE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.probeColumn.PROBE_DESCRIPTION));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.probeColumn.TYPE_PROBE));
            list.add(new Probe(depth, description, type));
        }
        cursor.close();
        db.close();
        return list;
    }

    public static void deleteProbe(long idProbe, Context context) {
        ObjectDB objectDB = new ObjectDB(context);
        SQLiteDatabase db = objectDB.getWritableDatabase();
        db.delete(ObjectDB.dbTab.TAB_PROBE, ObjectDB.probeColumn.ID_PROBE + " = ?", new String[]{String.valueOf(idProbe)});
        db.close();
    }


    public static double getMaxDepthExc(Context context, long keyExc) {
        ObjectDB db = new ObjectDB(context);
        Cursor cursor = db.getLayerMaxDepth(keyExc);
        double maxDepth = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                maxDepth = cursor.getDouble(cursor.getColumnIndexOrThrow(ObjectDB.layerColumn.END_LAYER));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return maxDepth;
    }


}
