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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.gmail.fomichov.m.drillingmagazine.R;
import com.gmail.fomichov.m.drillingmagazine.database.QueryProcessing;

public class DialogProbe extends DialogFragment {
    private static boolean newProbe, editProbe;
    private String title;
    private Spinner spTypeProbe;
    private EditText etDepthProbe;
    private EditText etDescriptionProbe;
    private View view;

    public static DialogProbe newInstance(String title, String nameObj, long keyExc) {
        editProbe = false;
        newProbe = false;
        newProbe = true;
        DialogProbe dialogProbe = new DialogProbe();
        dialogProbe.setCancelable(false);
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("nameObj", nameObj);
        args.putLong("keyExc", keyExc);
        dialogProbe.setArguments(args);
        return dialogProbe;
    }

    public static DialogProbe newInstance(long idProbe, String title, String typeProbe, String description, double depthProbe) {
        editProbe = false;
        newProbe = false;
        editProbe = true;
        DialogProbe dialogProbe = new DialogProbe();
        dialogProbe.setCancelable(false);
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("typeProbe", typeProbe);
        args.putString("description", description);
        args.putLong("idProbe", idProbe);
        args.putDouble("depthProbe", depthProbe);
        dialogProbe.setArguments(args);
        return dialogProbe;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_probe, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        etDepthProbe = (EditText) view.findViewById(R.id.etDepthProbe);
        etDescriptionProbe = (EditText) view.findViewById(R.id.etDescriptionProbe);
        spTypeProbe = (Spinner) view.findViewById(R.id.spTypeProbe);
        if (newProbe) {
            title = getArguments().getString("title");
        }
        if (editProbe) {
            title = getArguments().getString("title");
            etDescriptionProbe.setText(getArguments().getString("description"));
            etDepthProbe.setText(String.valueOf(getArguments().getDouble("depthProbe")));
            // назначаем спинеру тп пробы
            String[] array = getResources().getStringArray(R.array.probe_array);
            for (int i = 0; i < array.length; i++) {
                if (array[i].equals(getArguments().getString("typeProbe"))) {
                    spTypeProbe.setSelection(i);
                    break;
                }
            }
        }
        builder.setTitle(title);
        builder.setView(view);
        if (editProbe) {
            builder.setIcon(R.drawable.ic_edit_black_24dp);
        } else {
            builder.setIcon(R.drawable.ic_add_black_24dp);
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
            final Button positiveButton = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (newProbe) {
                        if (testProbe()) {
                            QueryProcessing.addNewProbe(
                                    getArguments().getString("nameObj"),
                                    Double.parseDouble(etDepthProbe.getText().toString()),
                                    etDescriptionProbe.getText().toString(),
                                    getArguments().getLong("keyExc"),
                                    spTypeProbe.getSelectedItem().toString(),
                                    getActivity());
                            Intent intent = new Intent("pressButtonOkInDialogProbe");
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), R.string.nameProbeTest, Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (editProbe) {
                        if (testProbe()) {
                            QueryProcessing.editProbe(
                                    getArguments().getLong("idProbe"),
                                    Double.parseDouble(etDepthProbe.getText().toString()),
                                    etDescriptionProbe.getText().toString(),
                                    spTypeProbe.getSelectedItem().toString(),
                                    getActivity());
                            Intent intent = new Intent("pressButtonOkInDialogProbe");
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), R.string.nameProbeTest, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            });
        }
    }

    private Boolean testProbe() {
        boolean temp;
        if (etDepthProbe.getText().toString().trim().isEmpty()) {
            temp = false;
        } else {
            temp = true;
        }
        return temp;
    }
}
