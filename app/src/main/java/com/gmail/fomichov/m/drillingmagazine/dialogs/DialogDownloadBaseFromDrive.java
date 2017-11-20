package com.gmail.fomichov.m.drillingmagazine.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.gmail.fomichov.m.drillingmagazine.R;

public class DialogDownloadBaseFromDrive {
    public static AlertDialog getDialog(Activity activity, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setIcon(R.drawable.ic_cloud_download_black_24dp); // иконка
        builder.setTitle(R.string.title_download_base_dialog); // заголовок диалога
        builder.setMessage(R.string.text_download_base_dialog); // текст сообщения
        // закрываем диалог по нажатию на кнопку
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent("pressButtonOkInDialogObj");
                intent.putExtra("LOAD_DRIVE", true);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }
}
