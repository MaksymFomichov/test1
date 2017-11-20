package com.gmail.fomichov.m.drillingmagazine.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gmail.fomichov.m.drillingmagazine.R;
import com.gmail.fomichov.m.drillingmagazine.database.QueryProcessing;

import java.util.ArrayList;
import java.util.Locale;

public class DialogLayer extends DialogFragment {
    private static boolean newLayer, editLayer;
    private String title;
    private EditText etStartDepth;
    private EditText etEndDepth;
    private EditText etDescriptionLayer;
    private ImageButton ibVoiceKeyboard;
    private View view;
    private static final int REQ_CODE_SPEECH_INPUT = 100;

    public static DialogLayer newInstance(String title, String nameObj, String numberExc, double startDepth, long idExc) {
        editLayer = false;
        newLayer = false;
        newLayer = true;
        DialogLayer dialogLayer = new DialogLayer();
        dialogLayer.setCancelable(false);
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("nameObj", nameObj);
        args.putString("numberExc", numberExc);
        args.putDouble("startDepth", startDepth);
        args.putLong("idExc", idExc);
        dialogLayer.setArguments(args);
        return dialogLayer;
    }

    public static DialogLayer newInstance(long idLayer, String title, String description, double startDepth, double endDepth) {
        editLayer = false;
        newLayer = false;
        editLayer = true;
        DialogLayer dialogLayer = new DialogLayer();
        dialogLayer.setCancelable(false);
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("description", description);
        args.putLong("idLayer", idLayer);
        args.putDouble("startDepth", startDepth);
        args.putDouble("endDepth", endDepth);
        dialogLayer.setArguments(args);
        return dialogLayer;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_layer, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        etStartDepth = (EditText) view.findViewById(R.id.etStartDepth);
        etEndDepth = (EditText) view.findViewById(R.id.etEndDepth);
        etDescriptionLayer = (EditText) view.findViewById(R.id.etDescriptionObject);

        ibVoiceKeyboard = (ImageButton) view.findViewById(R.id.ibVoiceKeyboard);

        ibVoiceKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

        if (newLayer) {
            title = getArguments().getString("title");
            etStartDepth.setText(String.valueOf(getArguments().getDouble("startDepth")));
        }
        if (editLayer) {
            title = getArguments().getString("title");
            etStartDepth.setText(String.valueOf(getArguments().getDouble("startDepth")));
            etEndDepth.setText(String.valueOf(getArguments().getDouble("endDepth")));
            etDescriptionLayer.setText(getArguments().getString("description"));
            etDescriptionLayer.setSelection(getArguments().getString("description").length()); // показывает курсор в конце текста
        }
        builder.setTitle(title);
        builder.setView(view);
        if (editLayer) {
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

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources().getString(R.string.voice_message_layer));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    etDescriptionLayer.setText(result.get(0));
                }
                break;
            }
        }
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
                    if (newLayer) {
                        if (testLayer()) {
                            QueryProcessing.addNewLayer(
                                    getArguments().getString("nameObj"),
                                    Double.parseDouble(etStartDepth.getText().toString()),
                                    Double.parseDouble(etEndDepth.getText().toString()),
                                    etDescriptionLayer.getText().toString(),
                                    getArguments().getLong("idExc"),
                                    getActivity());
                            Intent intent = new Intent("pressButtonOkInDialogLayer");
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), R.string.nameLayerTest, Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (editLayer) {
                        if (testLayer()) {
                            QueryProcessing.editLayer(
                                    Double.parseDouble(etStartDepth.getText().toString()),
                                    Double.parseDouble(etEndDepth.getText().toString()),
                                    etDescriptionLayer.getText().toString(),
                                    getArguments().getLong("idLayer"),
                                    getActivity());
                            Intent intent = new Intent("pressButtonOkInDialogLayer");
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), R.string.nameLayerTest, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            });
        }
    }

    private Boolean testLayer() {
        boolean temp;
        if (etStartDepth.getText().toString().trim().isEmpty() || etEndDepth.getText().toString().trim().isEmpty()
                || Double.parseDouble(etStartDepth.getText().toString()) >= Double.parseDouble(etEndDepth.getText().toString())) {
            temp = false;
        } else {
            temp = true;
        }
        return temp;
    }
}
