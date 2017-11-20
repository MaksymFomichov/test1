package com.gmail.fomichov.m.drillingmagazine.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaCodec;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gmail.fomichov.m.drillingmagazine.R;
import com.gmail.fomichov.m.drillingmagazine.database.QueryProcessing;
import com.gmail.fomichov.m.drillingmagazine.geology.ObjectGeology;
import com.gmail.fomichov.m.drillingmagazine.utils.CheckString;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DialogObj extends DialogFragment {
    private static boolean newObj, editObj, loadObj;
    private String title;
    private EditText etNameObject;
    private EditText etDescriptionObject;
    private List<ObjectGeology> list;

    // новый обьект
    public static DialogObj newInstance(String title) {
        editObj = false;
        loadObj = false;
        newObj = false;
        newObj = true;
        DialogObj dialogObj = new DialogObj();
        dialogObj.setCancelable(false);
        Bundle args = new Bundle();
        args.putString("title", title);
        dialogObj.setArguments(args);
        return dialogObj;
    }

    // редактирование обьекта
    public static DialogObj newInstance(String title, String name, String description) {
        editObj = false;
        loadObj = false;
        newObj = false;
        editObj = true;
        DialogObj dialogObj = new DialogObj();
        dialogObj.setCancelable(false);
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("name", name);
        args.putString("description", description);
        dialogObj.setArguments(args);
        return dialogObj;
    }

    // загрузка обьекта
    public static DialogObj newInstanceLoad(String title, String name, String description) {
        editObj = false;
        newObj = false;
        loadObj = false;
        loadObj = true;
        DialogObj dialogObj = new DialogObj();
        dialogObj.setCancelable(false);
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("name", name);
        args.putString("description", description);
        dialogObj.setArguments(args);
        return dialogObj;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_obj, null);
        list = QueryProcessing.getNameObjects(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        etNameObject = (EditText) view.findViewById(R.id.etNameObject);
        etDescriptionObject = (EditText) view.findViewById(R.id.etDescriptionObject);
        if (newObj) {
            title = getArguments().getString("title");
        }
        if (editObj || loadObj) {
            title = getArguments().getString("title");
            etDescriptionObject.setText(getArguments().getString("description"));
            etNameObject.setText(getArguments().getString("name"));
        }
        builder.setTitle(title);
        builder.setView(view);
        if (editObj) {
            builder.setIcon(R.drawable.ic_edit_black_24dp);
        } else if (editObj){
            builder.setIcon(R.drawable.ic_add_black_24dp);
        } else if (loadObj){
            builder.setIcon(R.drawable.ic_content_copy_black_24dp);
        }
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // переопределили в onResume (пишут в инете, что нужно оставить в основном поле для совместимости)
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        // переопределяем кнопку ок, для проверки вводимого значения
        // http://qaru.site/questions/13601/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked
        final AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog != null) {
            Button positiveButton = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (newObj || loadObj) {
                        if (testNewName()) {
                            // передаем новые значене и записываем их в базу
                            QueryProcessing.addNewObject(
                                    etNameObject.getText().toString(),
                                    etDescriptionObject.getText().toString(),
                                    getActivity());
                            Intent intent = new Intent("pressButtonOkInDialogObj");
                            if(loadObj){
                                intent.putExtra("LOAD_OBJ", true);
                                intent.putExtra("NAME_OBJ", etNameObject.getText().toString());
                            } else {
                                intent.putExtra("LOAD_OBJ", false);
                            }
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), R.string.nameObjTest, Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (editObj) {
                        if (testEditName()) {
                            // передаем старое название для поиска в базе и новые параметры
                            QueryProcessing.editObject(
                                    getArguments().getString("name"),
                                    etNameObject.getText().toString(),
                                    etDescriptionObject.getText().toString(),
                                    getActivity());
                            Intent intent = new Intent("pressButtonOkInDialogObj");
                            intent.putExtra("LOAD_OBJ", false);
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), R.string.nameObjTest, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            });
        }
    }

    // проверяем имя обьекта на пусутую стороку и на одинаковость
    private Boolean testNewName() {
        boolean temp = false;
        if (list.size() > 0) {
            for (ObjectGeology value : list) {
                if (value.getName().equals(etNameObject.getText().toString()) || etNameObject.getText().toString().trim().isEmpty() || !CheckString.isFileNameCorrect(etNameObject.getText().toString())) {
                    temp = false;
                    break;
                } else {
                    temp = true;
                }
            }
        } else {
            if (etNameObject.getText().toString().trim().isEmpty() || !CheckString.isFileNameCorrect(etNameObject.getText().toString())) {
                temp = false;
            } else {
                temp = true;
            }
        }
        return temp;
    }

    // проверяем имя обьекта на пусутую стороку если имя обьекта совпадает с текущим то все норм, если меняется то проверить на совпадения
    private Boolean testEditName() {
        boolean temp = false;
        for (ObjectGeology value : list) {
            if (getArguments().getString("name").equals(etNameObject.getText().toString())) {
                temp = true;
                break;
            } else if (!CheckString.isFileNameCorrect(etNameObject.getText().toString())) {
                temp = false;
                break;
            } else if (value.getName().equals(etNameObject.getText().toString())) {
                temp = false;
                break;
            } else if (etNameObject.getText().toString().trim().isEmpty()) {
                temp = false;
                break;
            } else {
                temp = true;
            }
        }
        return temp;
    }
}
