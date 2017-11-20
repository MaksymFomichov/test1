package com.gmail.fomichov.m.drillingmagazine.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gmail.fomichov.m.drillingmagazine.database.QueryProcessing;
import com.gmail.fomichov.m.drillingmagazine.fragments.excavation.ExcFrag;
import com.gmail.fomichov.m.drillingmagazine.geology.Excavation;
import com.gmail.fomichov.m.drillingmagazine.geology.Layer;
import com.gmail.fomichov.m.drillingmagazine.geology.ObjectGeology;
import com.gmail.fomichov.m.drillingmagazine.geology.Probe;
import com.gmail.fomichov.m.drillingmagazine.pdf.CreatePdfExc;
import com.itextpdf.text.DocumentException;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class JsonGenerate {
    private final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private int choice;

    public void writeFileExc(final int position, final String nameObject, final Context context, final Activity activity) {
        choice = 2;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            final FutureTask<String> stringFutureTask = new FutureTask<String>(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    Excavation excavation = getFullDataExc(context, position, nameObject);
                    return JSON.toJSONString(excavation);
                }
            });
            new Thread(stringFutureTask).start();
            final String nameFile = ExcFrag.list.get(position).getTypeExcavation() + "-" + ExcFrag.list.get(position).getNameExcavation() + " " + nameObject;
            if (isExternalStorageWritable()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            try {
                                getFolderStorageDir(stringFutureTask.get(), nameFile, context, activity, nameObject, 0); // 0 вместо позиции, лень переделывать - возможно потом доведу до ума
                            } catch (DocumentException e) {
                                e.printStackTrace();
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }

    // получаем обьект выработки включая слои и пробы
    public Excavation getFullDataExc(Context context, int position, String nameObject) {
        List<Probe> probeList = QueryProcessing.getListProbeJSON(context, ExcFrag.list.get(position).getKeyExc());
        List<Layer> layerList = QueryProcessing.getListLayerJSON(context, ExcFrag.list.get(position).getKeyExc());
        Excavation excavation = new Excavation(
                ExcFrag.list.get(position).getNameExcavation(), ExcFrag.list.get(position).getDateStart(),
                ExcFrag.list.get(position).getDateEnd(), probeList, layerList, ExcFrag.list.get(position).getDescriptionExcavation(),
                ExcFrag.list.get(position).getLatitude(), ExcFrag.list.get(position).getLongitude(), ExcFrag.list.get(position).getEquipment(),
                ExcFrag.list.get(position).getWhoWorked(), ExcFrag.list.get(position).getPenetrationMethod(), ExcFrag.list.get(position).getTypeExcavation(),
                nameObject, ExcFrag.list.get(position).getAbsoluteElevation(), ExcFrag.list.get(position).getWaterShow(), ExcFrag.list.get(position).getWaterStay(),
                ExcFrag.list.get(position).getDateWaterStay(),
                ExcFrag.list.get(position).getDateWaterShow());
        return excavation;
    }

    // записываем обьект в файл
    public void writeFileObj(final String nameObject, final Context context, final Activity activity, final String description) {
        choice = 1;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            final FutureTask<String> stringFutureTask = new FutureTask<String>(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    List<Excavation> tempList = QueryProcessing.getListExcavation(context, nameObject);
                    List<Excavation> excavationList = new ArrayList<>();
                    for (Excavation value : tempList) {
                        List<Probe> probeList = QueryProcessing.getListProbeJSON(context, value.getKeyExc());
                        List<Layer> layerList = QueryProcessing.getListLayerJSON(context, value.getKeyExc());
                        Excavation excavation = new Excavation(
                                value.getNameExcavation(), value.getDateStart(),
                                value.getDateEnd(), probeList, layerList, value.getDescriptionExcavation(),
                                value.getLatitude(), value.getLongitude(), value.getEquipment(),
                                value.getWhoWorked(), value.getPenetrationMethod(), value.getTypeExcavation(),
                                nameObject, value.getAbsoluteElevation(), value.getWaterShow(), value.getWaterStay(),
                                value.getDateWaterStay(),
                                value.getDateWaterShow());
                        excavationList.add(excavation);
                    }
                    ObjectGeology objectGeology = new ObjectGeology(nameObject, description, excavationList);
                    return JSON.toJSONString(objectGeology);
                }
            });
            new Thread(stringFutureTask).start();
            final String nameFile = nameObject;
            if (isExternalStorageWritable()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            try {
                                getFolderStorageDir(stringFutureTask.get(), nameFile, context, activity, nameObject, 0); // 0 вместо позиции, лень переделывать - возможно потом доведу до ума
                            } catch (DocumentException e) {
                                e.printStackTrace();
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }

    // получаем json всей базы
    public String getJsonAllBase(final Context context) throws ExecutionException, InterruptedException {
        final FutureTask<String> stringFutureTask = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                List<ObjectGeology> nameObject = QueryProcessing.getListObjects(context);
                List<ObjectGeology> list = new ArrayList<>();
                int sizeListNameObject = nameObject.size();
                for (int i = 0; i < sizeListNameObject; i++) {
                    List<Excavation> tempList = QueryProcessing.getListExcavation(context, nameObject.get(i).getName());
                    List<Excavation> excavationList = new ArrayList<>();
                    for (Excavation value : tempList) {
                        List<Probe> probeList = QueryProcessing.getListProbeJSON(context, value.getKeyExc());
                        List<Layer> layerList = QueryProcessing.getListLayerJSON(context, value.getKeyExc());
                        Excavation excavation = new Excavation(
                                value.getNameExcavation(), value.getDateStart(),
                                value.getDateEnd(), probeList, layerList, value.getDescriptionExcavation(),
                                value.getLatitude(), value.getLongitude(), value.getEquipment(),
                                value.getWhoWorked(), value.getPenetrationMethod(), value.getTypeExcavation(),
                                nameObject.get(i).getName(), value.getAbsoluteElevation(), value.getWaterShow(), value.getWaterStay(),
                                value.getDateWaterStay(),
                                value.getDateWaterShow());
                        excavationList.add(excavation);
                    }
                    list.add(new ObjectGeology( nameObject.get(i).getName(),  nameObject.get(i).getDescription(), excavationList));
                }
             return JSON.toJSONString(list);
            }
        });
        new Thread(stringFutureTask).start();
        return stringFutureTask.get();
    }

    public void createFilePdfExc(final int position, final String nameObject, final Context context, final Activity activity){
        choice = 3;
        final String nameFile = ExcFrag.list.get(position).getTypeExcavation() + "-" + ExcFrag.list.get(position).getNameExcavation() + " " + nameObject;
        if (isExternalStorageWritable()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        getFolderStorageDir("", nameFile, context, activity, nameObject, position);
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    // создаем каталог и файл
    private File getFolderStorageDir(String json, final String nameFile, final Context context, Activity activity, final String nameObject, int position) throws DocumentException {
        final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "DrillingMagazine/" + nameObject);
        if (!file.mkdirs()) {
            MyLog.showLog("Directory not created");
        }
        File patch = null;
        if (choice == 2) {
            patch = new File(file, nameFile + ".exc");
        } else if (choice == 1) {
            patch = new File(file, nameFile + ".obj");
        } else if (choice == 3) {
            patch = new File(file, nameFile + ".pdf");
        }
        try {
            if (choice == 1 || choice == 2) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(patch));
                bw.write(json);
                bw.close();
            } else if (choice == 3) {
                Excavation excavation = getFullDataExc(context, position, nameObject);
                CreatePdfExc pdfExc = new CreatePdfExc(context, excavation, patch);
                pdfExc.startCreatePDF();
            }
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, "Файл " + nameFile + " создан в Documents/DrillingMagazine/" + nameObject, Toast.LENGTH_LONG).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    // Проверяет, доступно ли внешнее хранилище для чтения и записи
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // считываем данные из файла в стринг
    private String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    // получаем готовый json для парсинга в обьект
    public String getJSON(Intent data) {
        File file = new File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
        FileInputStream stream = null;
        String json = "";
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            json = convertStreamToString(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}
