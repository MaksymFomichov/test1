package com.gmail.fomichov.m.drillingmagazine.drive;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.gmail.fomichov.m.drillingmagazine.MainActivity;
import com.gmail.fomichov.m.drillingmagazine.R;
import com.gmail.fomichov.m.drillingmagazine.utils.JsonGenerate;
import com.gmail.fomichov.m.drillingmagazine.utils.MyLog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

// создаем файл в облаке
public class DriveUploadFileInFolder {
    public static final String APP_PREFERENCES_CREATE_FOLDER = "create_folder";
    private static final String APP_PREFERENCES_ID_FOLDER = "id_folder";
    private GoogleApiClient mGoogleApiClient;
    private Context context;

    public DriveUploadFileInFolder(GoogleApiClient mGoogleApiClient, Context context) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.context = context;
    }

    private void disconnectGoogleApiClient() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            MyLog.showLog("API-клиент отключен");
        }
    }

    public void saveFileToDrive() {
        if (!MainActivity.mSettings.contains(APP_PREFERENCES_CREATE_FOLDER)) {
            createFolder(); // создаем папку если раньше ее не создавали
        } else { // если папка уже создана проверяем ее на удаление
            checkFolder();
        }
    }

    private void writeFile() {
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

                        // форматируем вид даты для названия файла
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
                        MetadataChangeSet changeSetFile = new MetadataChangeSet.Builder()
                                .setTitle("dataBase_" + formatter.format(new Date()) + ".fma")
                                .setMimeType("text/plain")
                                .build();

                        String mapTypeString = MainActivity.mSettings.getString(APP_PREFERENCES_ID_FOLDER, "noFolder");
                        DriveId mFolderDriveId = DriveId.decodeFromString(mapTypeString);
                        DriveFolder folder = mFolderDriveId.asDriveFolder();
                        folder.createFile(mGoogleApiClient, changeSetFile, result.getDriveContents()).setResultCallback(fileCallback);
                        progressDialog.dismiss();
                    }
                }.start();
            }
        });
    }

    // создаем папку если раньше ее не создавали и записываем в настройки ее айди
    private void createFolder() {
        MetadataChangeSet changeSetFolder = new MetadataChangeSet.Builder().setTitle("DrillingMagazine").build();
        Drive.DriveApi.getRootFolder(mGoogleApiClient).createFolder(mGoogleApiClient, changeSetFolder).setResultCallback(folderCreatedCallback);
    }

    private ResultCallback<DriveFolder.DriveFolderResult> folderCreatedCallback = new
            ResultCallback<DriveFolder.DriveFolderResult>() {
                @Override
                public void onResult(DriveFolder.DriveFolderResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Toast.makeText(context, R.string.errorCreateFolder, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String idFolder = String.valueOf(result.getDriveFolder().getDriveId());
                    MyLog.showLog("Создал папку: " + idFolder);
                    // Запоминаем данные
                    saveInPreferencesIdFolder(idFolder);
                    writeFile();
                }
            };

    private void saveInPreferencesIdFolder(String idFolder) {
        SharedPreferences.Editor editor = MainActivity.mSettings.edit();
        editor.putBoolean(APP_PREFERENCES_CREATE_FOLDER, true);
        editor.putString(APP_PREFERENCES_ID_FOLDER, idFolder);
        editor.apply();
    }

    private void checkFolder() {
        DriveId folderId = DriveId.decodeFromString(MainActivity.mSettings.getString(APP_PREFERENCES_ID_FOLDER, "noFolder"));
        DriveFolder folder = Drive.DriveApi.getFolder(mGoogleApiClient, folderId);
        folder.getMetadata(mGoogleApiClient).setResultCallback(metadataRetrievedCallback);
    }

    final private ResultCallback<DriveResource.MetadataResult> metadataRetrievedCallback = new
            ResultCallback<DriveResource.MetadataResult>() {
                @Override
                public void onResult(DriveResource.MetadataResult result) {
                    if (!result.getStatus().isSuccess()) {
                        MyLog.showLog(MainActivity.mSettings.getString(APP_PREFERENCES_ID_FOLDER, "noFolder"));
                        Toast.makeText(context, R.string.problemGetMetadata, Toast.LENGTH_SHORT).show();
                        disconnectGoogleApiClient();
                        return;
                    }
                    Metadata metadata = result.getMetadata();
                    if (metadata.isTrashed()) {
                        MyLog.showLog("Папка удалена");
                        // обнуляем настройки
                        SharedPreferences.Editor editor = MainActivity.mSettings.edit();
                        editor.putString(APP_PREFERENCES_ID_FOLDER, "noFolder");
                        editor.apply();
                        // создаем папку заново
                        createFolder();
                    } else {
                        MyLog.showLog("Папка на месте");
                        writeFile();
                    }
                }
            };

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
}
