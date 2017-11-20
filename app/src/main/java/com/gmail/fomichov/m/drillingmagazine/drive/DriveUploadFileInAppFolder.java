package com.gmail.fomichov.m.drillingmagazine.drive;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.gmail.fomichov.m.drillingmagazine.R;
import com.gmail.fomichov.m.drillingmagazine.utils.JsonGenerate;
import com.gmail.fomichov.m.drillingmagazine.utils.MyLog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

// создаем файл в облаке в папке, которую гугл выделяет для приложения в обллаке
public class DriveUploadFileInAppFolder {
    private GoogleApiClient mGoogleApiClient;
    private Context context;

    public DriveUploadFileInAppFolder(GoogleApiClient mGoogleApiClient, Context context) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.context = context;
    }

    public void saveFileToDrive() {
        // Начните с создания нового содержимого и установки обратного вызова.
        MyLog.showLog("Создание нового содержимого.");
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(final DriveApi.DriveContentsResult result) {
                // Если операция не была успешной, мы ничего не можем сделать и должны потерпеть неудачу.
                if (!result.getStatus().isSuccess()) {
                    MyLog.showLog("Не удалось создать новое содержимое.");
                    return;
                }
                final ProgressDialog progressDialog = new ProgressDialog(context);
                final DriveContents driveContents = result.getDriveContents();
                progressDialog.setCancelable(false);
                progressDialog.setMessage(context.getResources().getString((R.string.msgProgressDialogUpload)));
                progressDialog.show();
                new Thread() {
                    @Override
                    public void run() {
                        // записать содержимое на накопитель
                        OutputStream outputStream = driveContents.getOutputStream();
                        Writer writer = new OutputStreamWriter(outputStream);
                        try {
                            try {
                                writer.write(new JsonGenerate().getJsonAllBase(context));
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            writer.close();
                        } catch (IOException e) {
                            MyLog.showLog(e.getMessage());
                        }

                        MetadataChangeSet changeSetFile = new MetadataChangeSet.Builder()
                                .setTitle("dataBase_drillingMagazine.fma")
                                .setMimeType("text/plain")
                                .setStarred(true).build();

                        Drive.DriveApi.getAppFolder(mGoogleApiClient)
                                .createFile(mGoogleApiClient, changeSetFile, result.getDriveContents())
                                .setResultCallback(fileCallback);
                        progressDialog.dismiss();
                    }
                }.start();
            }
        });
    }

    private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Toast.makeText(context, R.string.errorUploadDataBase, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    MyLog.showLog("Создал файл с контентом: " + result.getDriveFile().getDriveId());
                    Toast.makeText(context, R.string.goodUploadDataBase, Toast.LENGTH_SHORT).show();
                    disconnectGoogleApiClient();
                }
            };

    private void disconnectGoogleApiClient() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            MyLog.showLog("API-клиент отключен");
        }
    }
}
