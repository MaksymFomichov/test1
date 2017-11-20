package com.gmail.fomichov.m.drillingmagazine.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.gmail.fomichov.m.drillingmagazine.R;

public class DialogAbout {

    public static AlertDialog getDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setIcon(R.mipmap.ic_launcher); // иконка
        builder.setTitle(R.string.title_about_dialog); // заголовок иалога
        builder.setMessage(R.string.text_about_dialog); // текст сообщения
        // закрываем диалог по нажатию на кнопку
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
