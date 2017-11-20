package com.gmail.fomichov.m.drillingmagazine.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.fomichov.m.drillingmagazine.R;
import com.gmail.fomichov.m.drillingmagazine.database.QueryProcessing;

public class DialogDelete extends DialogFragment {
    private int typeObjectBD;
    private String textDelete; // изменение окончания у фразы для тоста в зависимости от падежа
    private String textName; // обьединение выработки и номера

    // для слоя
    public static DialogDelete newInstance(double depthStartLayer, double depthEndLayer, long idLayer, String type,  String toast, int typeObjectBD, String message) {
        DialogDelete dialogDelete = new DialogDelete();
        dialogDelete.setCancelable(false);
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putString("toast", toast);
        args.putDouble("depthStartLayer", depthStartLayer);
        args.putDouble("depthEndLayer", depthEndLayer);
        args.putLong("idLayer", idLayer);
        args.putString("type", type);
        args.putInt("typeObjectBD", typeObjectBD);
        dialogDelete.setArguments(args);
        return dialogDelete;
    }

    // для пробы
    public static DialogDelete newInstance(double depthProbe, int typeObjectBD, long idProbe, String message,  String toast, String type) {
        DialogDelete dialogDelete = new DialogDelete();
        dialogDelete.setCancelable(false);
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putInt("typeObjectBD", typeObjectBD);
        args.putString("toast", toast);
        args.putString("type", type);
        args.putDouble("depthProbe", depthProbe);
        args.putLong("idProbe", idProbe);
        dialogDelete.setArguments(args);
        return dialogDelete;
    }

    // для выработки
    public static DialogDelete newInstance(int typeObjectBD, String message, String typeExcavation, String name, String type, String toast, long idExc) {
        DialogDelete dialogDelete = new DialogDelete();
        dialogDelete.setCancelable(false);
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putString("name", name);
        args.putString("typeExcavation", typeExcavation);
        args.putString("type", type);
        args.putString("toast", toast);
        args.putInt("typeObjectBD", typeObjectBD);
        args.putLong("idExc", idExc);
        dialogDelete.setArguments(args);
        return dialogDelete;
    }

    // для обьекта
    public static DialogDelete newInstance(int typeObjectBD, String message, String name, String type, String toast) {
        DialogDelete dialogDelete = new DialogDelete();
        dialogDelete.setCancelable(false);
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putString("name", name);
        args.putString("type", type);
        args.putString("toast", toast);
        args.putInt("typeObjectBD", typeObjectBD);
        dialogDelete.setArguments(args);
        return dialogDelete;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        typeObjectBD = getArguments().getInt("typeObjectBD");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        builder.setTitle(getString(R.string.title_delete_dialog) + " " + getArguments().getString("type"));
        builder.setIcon(R.drawable.ic_warning_red_24dp);

        if (typeObjectBD == 1) {
            textDelete = getString(R.string.delete_word_he);
            textName = getArguments().getString("name");
        } else if (typeObjectBD == 2) {
            textDelete = getString(R.string.delete_word_she);
            textName = getArguments().getString("typeExcavation") + " " + getArguments().getString("name");
        } else if (typeObjectBD == 3) {
            textDelete = getString(R.string.delete_word_she);
            textName = String.valueOf(getArguments().getDouble("depthProbe"));
        } else if (typeObjectBD == 4) {
            textDelete = getString(R.string.delete_word_he);
            textName = String.valueOf(getArguments().getDouble("depthStartLayer")) + " - " + String.valueOf(getArguments().getDouble("depthEndLayer"));
        }
        String textDialog;
        if (typeObjectBD == 3) {
            textDialog = getArguments().getString("message") + " " + textName + "м " + getString(R.string.delete_1);
        } else {
            textDialog = getArguments().getString("message") + " \"" + textName + "\" " + getString(R.string.delete_1);
        }
        tvMessage.setText(textDialog);
        builder.setView(view);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                if (typeObjectBD == 1) {
                    QueryProcessing.deleteObject(getArguments().getString("name"), getActivity());
                    intent = new Intent("pressButtonOkInDialogObj");
                    intent.putExtra("LOAD_OBJ", false);
                } else if (typeObjectBD == 2) {
                    QueryProcessing.deleteExcavation(getArguments().getLong("idExc"), getActivity());
                    intent = new Intent("pressButtonOkInDialogExc");
                } else if (typeObjectBD == 3) {
                    QueryProcessing.deleteProbe(getArguments().getLong("idProbe"), getActivity());
                    intent = new Intent("pressButtonOkInDialogProbe");
                } else if (typeObjectBD == 4){
                    QueryProcessing.deleteLayer(getArguments().getLong("idLayer"), getActivity());
                    intent = new Intent("pressButtonOkInDialogLayer");
                }

                Toast.makeText(getActivity(), getArguments().getString("toast") + " \"" + textName + "\" " + textDelete, Toast.LENGTH_SHORT).show();
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
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
}
