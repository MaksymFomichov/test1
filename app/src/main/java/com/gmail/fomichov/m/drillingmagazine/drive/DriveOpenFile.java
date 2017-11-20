package com.gmail.fomichov.m.drillingmagazine.drive;

import android.app.ProgressDialog;
import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.gmail.fomichov.m.drillingmagazine.R;
import com.gmail.fomichov.m.drillingmagazine.database.QueryProcessing;
import com.gmail.fomichov.m.drillingmagazine.fragments.ObjFragList;
import com.gmail.fomichov.m.drillingmagazine.geology.Excavation;
import com.gmail.fomichov.m.drillingmagazine.geology.ObjectGeology;
import com.gmail.fomichov.m.drillingmagazine.utils.MyLog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class DriveOpenFile {
    private DriveId mSelectedFileDriveId;
    private GoogleApiClient mGoogleApiClient;
    private Context context;

    public DriveOpenFile(DriveId mSelectedFileDriveId, GoogleApiClient mGoogleApiClient, Context context) {
        this.mSelectedFileDriveId = mSelectedFileDriveId;
        this.mGoogleApiClient = mGoogleApiClient;
        this.context = context;
    }

    public void open() {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                DriveFile.DownloadProgressListener listener = new DriveFile.DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long bytesExpected) {
                        int progress = (int) (bytesDownloaded * 100 / bytesExpected);
                        progressDialog.setMessage(String.format("Loading progress: %d percent", progress));
                    }
                };
                DriveFile driveFile = mSelectedFileDriveId.asDriveFile();
                driveFile.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, listener).setResultCallback(driveContentsCallback);
                mSelectedFileDriveId = null;
                progressDialog.dismiss();
            }
        }).start();
    }

    private void disconnectGoogleApiClient() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            MyLog.showLog("API-клиент отключен");
        }
    }

    private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        MyLog.showLog("Ошибка при открытии содержимого файла");
                        return;
                    }
                    MyLog.showLog("Открыто содержимое файла");
                    disconnectGoogleApiClient();
                    DriveContents contents = result.getDriveContents();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(contents.getInputStream()));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    try {
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        loadObjectsFromDrive(builder.toString());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

    private void loadObjectsFromDrive(final String json) throws InterruptedException {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString((R.string.msgProgressDialog)));
        progressDialog.show();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<ObjectGeology> objectGeologyList = JSON.parseArray(json, ObjectGeology.class);
                for (ObjectGeology value : objectGeologyList) {
                    String newNameObj = value.getName();
                    QueryProcessing.addNewObject(newNameObj, value.getDescription(), context);
                    int countExcavation = value.getExcavations().size();
                    for (int i = 0; i < countExcavation; i++) {
                        Excavation tempExc = value.getExcavations().get(i);
                        QueryProcessing.addNewExcavation(
                                newNameObj,
                                tempExc.getTypeExcavation(),
                                tempExc.getNameExcavation(),
                                tempExc.getDateStart(),
                                tempExc.getDateEnd(),
                                tempExc.getDateWaterStay(),
                                tempExc.getDateWaterShow(),
                                tempExc.getLatitude(),
                                tempExc.getLongitude(),
                                tempExc.getDescriptionExcavation(),
                                tempExc.getWhoWorked(),
                                tempExc.getEquipment(),
                                tempExc.getPenetrationMethod(),
                                tempExc.getWaterShow(),
                                tempExc.getWaterStay(),
                                tempExc.getAbsoluteElevation(),
                                context);
                        long newExcId = QueryProcessing.getNewExcavationId(context);
                        int countLayer = tempExc.getLayers().size();
                        for (int j = 0; j < countLayer; j++) {
                            QueryProcessing.addNewLayer(
                                    newNameObj,
                                    tempExc.getLayers().get(j).getStartDepth(),
                                    tempExc.getLayers().get(j).getEndDepth(),
                                    tempExc.getLayers().get(j).getDescription(),
                                    newExcId,
                                    context);
                        }
                        int countProbe = tempExc.getProbes().size();
                        for (int k = 0; k < countProbe; k++) {
                            QueryProcessing.addNewProbe(
                                    newNameObj,
                                    tempExc.getProbes().get(k).getDepth(),
                                    tempExc.getProbes().get(k).getDescription(),
                                    newExcId,
                                    tempExc.getProbes().get(k).getType(),
                                    context);
                        }
                    }
                    MyLog.showLog(newNameObj);
                }
                progressDialog.dismiss();
            }
        });
        thread.start();
        thread.join();
        ObjFragList.updateRecyclerView();
    }
}
