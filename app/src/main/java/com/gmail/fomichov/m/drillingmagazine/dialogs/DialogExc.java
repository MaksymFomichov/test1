package com.gmail.fomichov.m.drillingmagazine.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.fomichov.m.drillingmagazine.R;
import com.gmail.fomichov.m.drillingmagazine.database.QueryProcessing;
import com.gmail.fomichov.m.drillingmagazine.geology.Excavation;
import com.gmail.fomichov.m.drillingmagazine.utils.CheckString;
import com.gmail.fomichov.m.drillingmagazine.utils.MyDateUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.text.ParseException;
import java.util.List;

public class DialogExc extends DialogFragment implements GoogleApiClient.OnConnectionFailedListener {
    private static boolean newExc, editExc, loadExc, copyExc;
    private TextView tvDateStart;
    private TextView tvDateEnd;
    private TextView tvDateWaterStay;
    private TextView tvDateWaterShow;
    private EditText etLatitude;
    private EditText etLongitude;
    private EditText etDescriptionExcavation;
    private EditText etWho;
    private EditText etThan;
    private EditText etHow;
    private EditText etAbsoluteElevation;
    private String title;
    private String dateStart;
    private String dateEnd;
    private String dateWaterStay;
    private String dateWaterShow;
    private EditText etNumberExc;
    private EditText etShowWater;
    private EditText etStayWater;
    private ImageButton ibGetCoordinate;
    private Spinner spTypeExc;
    private int tempChoice; // нужен для правильного изменения тккст вью дат
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    private List<Excavation> list;

    // новая выработка
    public static DialogExc newInstance(String title, String date, String nameObject) {
        newExc = false;
        editExc = false;
        copyExc = false;
        loadExc = false;
        newExc = true;
        DialogExc dialogExc = new DialogExc();
        dialogExc.setCancelable(false);
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("dateStart", date);
        args.putString("dateEnd", date);
        args.putString("dateWaterStay", date);
        args.putString("dateWaterShow", date);
        args.putString("nameObject", nameObject);
        dialogExc.setArguments(args);
        return dialogExc;
    }

    // редактирование выработки
    public static DialogExc newInstance(long idExc, String title, String dateStart, String dateEnd, String dateWaterStay, String dateWaterShow, String latitude, String longitude, String description,
                                        String who, String than, String how, String number, String type, String nameObject, double absoluteElevation, double showWater, double stayWater) {
        newExc = false;
        editExc = false;
        copyExc = false;
        loadExc = false;
        editExc = true;
        DialogExc dialogExc = new DialogExc();
        dialogExc.setCancelable(false);
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("dateStart", dateStart);
        args.putString("dateEnd", dateEnd);
        args.putString("dateWaterStay", dateWaterStay);
        args.putString("dateWaterShow", dateWaterShow);
        args.putString("latitude", latitude);
        args.putString("longitude", longitude);
        args.putString("description", description);
        args.putString("who", who);
        args.putString("than", than);
        args.putString("how", how);
        args.putString("number", number);
        args.putString("type", type);
        args.putString("nameObject", nameObject);
        args.putDouble("absoluteElevation", absoluteElevation);
        args.putDouble("showWater", showWater);
        args.putDouble("stayWater", stayWater);
        args.putLong("idExc", idExc);
        dialogExc.setArguments(args);
        return dialogExc;
    }

    // загрузка выработки
    public static DialogExc newInstance(String title, String dateStart, String dateEnd, String dateWaterStay, String dateWaterShow, String latitude, String longitude, String description,
                                        String who, String than, String how, String number, String type, String nameObject, double absoluteElevation, double showWater, double stayWater) {
        newExc = false;
        editExc = false;
        copyExc = false;
        loadExc = false;
        loadExc = true;
        DialogExc dialogExc = new DialogExc();
        dialogExc.setCancelable(false);
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("dateStart", dateStart);
        args.putString("dateEnd", dateEnd);
        args.putString("dateWaterStay", dateWaterStay);
        args.putString("dateWaterShow", dateWaterShow);
        args.putString("latitude", latitude);
        args.putString("longitude", longitude);
        args.putString("description", description);
        args.putString("who", who);
        args.putString("than", than);
        args.putString("how", how);
        args.putString("number", number);
        args.putString("type", type);
        args.putString("nameObject", nameObject);
        args.putDouble("absoluteElevation", absoluteElevation);
        args.putDouble("showWater", showWater);
        args.putDouble("stayWater", stayWater);
        dialogExc.setArguments(args);
        return dialogExc;
    }

    // копирование выработки
    public static DialogExc newInstance(String title, long idExc, String dateStart, String dateEnd, String dateWaterStay, String dateWaterShow, String latitude, String longitude, String description,
                                        String who, String than, String how, String number, String type, String nameObject, double absoluteElevation, double showWater, double stayWater) {
        newExc = false;
        editExc = false;
        loadExc = false;
        copyExc = false;
        copyExc = true;
        DialogExc dialogExc = new DialogExc();
        dialogExc.setCancelable(false);
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("dateStart", dateStart);
        args.putString("dateEnd", dateEnd);
        args.putString("dateWaterStay", dateWaterStay);
        args.putString("dateWaterShow", dateWaterShow);
        args.putString("latitude", latitude);
        args.putString("longitude", longitude);
        args.putString("description", description);
        args.putString("who", who);
        args.putString("than", than);
        args.putString("how", how);
        args.putString("number", number);
        args.putString("type", type);
        args.putString("nameObject", nameObject);
        args.putDouble("absoluteElevation", absoluteElevation);
        args.putDouble("showWater", showWater);
        args.putDouble("stayWater", stayWater);
        args.putLong("idExc", idExc);
        dialogExc.setArguments(args);
        return dialogExc;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("pressButtonOkInDialogDate"));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_exc, null);

        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        etNumberExc = (EditText) view.findViewById(R.id.etNumberExc);
        etDescriptionExcavation = (EditText) view.findViewById(R.id.etDescriptionExcavation);
        tvDateStart = (TextView) view.findViewById(R.id.tvStartDate);
        tvDateEnd = (TextView) view.findViewById(R.id.tvEndDate);
        tvDateWaterStay = (TextView) view.findViewById(R.id.tvDateWaterStay);
        tvDateWaterShow = (TextView) view.findViewById(R.id.tvDateWaterShow);
        etLatitude = (EditText) view.findViewById(R.id.etLatitude);
        etLongitude = (EditText) view.findViewById(R.id.etLongitude);
        etWho = (EditText) view.findViewById(R.id.etWho);
        etThan = (EditText) view.findViewById(R.id.etThan);
        etHow = (EditText) view.findViewById(R.id.etHow);
        etAbsoluteElevation = (EditText) view.findViewById(R.id.etAbsoluteElevation);
        etShowWater = (EditText) view.findViewById(R.id.etShowWater);
        etStayWater = (EditText) view.findViewById(R.id.etStayWater);
        ibGetCoordinate = (ImageButton) view.findViewById(R.id.ibGetCoordinate);
        spTypeExc = (Spinner) view.findViewById(R.id.spTypeExc);

        list = QueryProcessing.getListExcavation(getActivity(), getArguments().getString("nameObject"));

        if (newExc) {
            title = getArguments().getString("title");
            dateStart = getArguments().getString("dateStart");
            dateEnd = getArguments().getString("dateEnd");
            dateWaterStay = getArguments().getString("dateWaterStay");
            dateWaterShow = getArguments().getString("dateWaterShow");
        }
        if (editExc) {
            title = getArguments().getString("title");
            dateStart = getArguments().getString("dateStart");
            dateEnd = getArguments().getString("dateEnd");
            dateWaterStay = getArguments().getString("dateWaterStay");
            dateWaterShow = getArguments().getString("dateWaterShow");
            etLatitude.setText(getArguments().getString("latitude"));
            etLongitude.setText(getArguments().getString("longitude"));
            etDescriptionExcavation.setText(getArguments().getString("description"));
            etDescriptionExcavation.setText(getArguments().getString("description"));
            etWho.setText(getArguments().getString("who"));
            etThan.setText(getArguments().getString("than"));
            etHow.setText(getArguments().getString("how"));
            etNumberExc.setText(getArguments().getString("number"));
            etAbsoluteElevation.setText(String.valueOf(getArguments().getDouble("absoluteElevation")));
            etShowWater.setText(String.valueOf(getArguments().getDouble("showWater")));
            etStayWater.setText(String.valueOf(getArguments().getDouble("stayWater")));
            // назначаем спинеру тп выработки
            String[] array = getResources().getStringArray(R.array.exc_array);
            for (int i = 0; i < array.length; i++) {
                if (array[i].equals(getArguments().getString("type"))) {
                    spTypeExc.setSelection(i);
                    break;
                }
            }
        }
        if (loadExc) {
            title = getArguments().getString("title");
            dateStart = getArguments().getString("dateStart");
            dateEnd = getArguments().getString("dateEnd");
            dateWaterStay = getArguments().getString("dateWaterStay");
            dateWaterShow = getArguments().getString("dateWaterShow");
            etLatitude.setText(getArguments().getString("latitude"));
            etLongitude.setText(getArguments().getString("longitude"));
            etDescriptionExcavation.setText(getArguments().getString("description"));
            etDescriptionExcavation.setText(getArguments().getString("description"));
            etWho.setText(getArguments().getString("who"));
            etThan.setText(getArguments().getString("than"));
            etHow.setText(getArguments().getString("how"));
            etNumberExc.setText(getArguments().getString("number"));
            etAbsoluteElevation.setText(String.valueOf(getArguments().getDouble("absoluteElevation")));
            etShowWater.setText(String.valueOf(getArguments().getDouble("showWater")));
            etStayWater.setText(String.valueOf(getArguments().getDouble("stayWater")));
            // назначаем спинеру тип выработки
            String[] array = getResources().getStringArray(R.array.exc_array);
            for (int i = 0; i < array.length; i++) {
                if (array[i].equals(getArguments().getString("type"))) {
                    spTypeExc.setSelection(i);
                    break;
                }
            }
        }
        if (copyExc) {
            title = getArguments().getString("title");
            dateStart = getArguments().getString("dateStart");
            dateEnd = getArguments().getString("dateEnd");
            dateWaterStay = getArguments().getString("dateWaterStay");
            dateWaterShow = getArguments().getString("dateWaterShow");
            etLatitude.setText(getArguments().getString("latitude"));
            etLongitude.setText(getArguments().getString("longitude"));
            etDescriptionExcavation.setText(getArguments().getString("description"));
            etDescriptionExcavation.setText(getArguments().getString("description"));
            etWho.setText(getArguments().getString("who"));
            etThan.setText(getArguments().getString("than"));
            etHow.setText(getArguments().getString("how"));
            etNumberExc.setText(getArguments().getString("number"));
            etAbsoluteElevation.setText(String.valueOf(getArguments().getDouble("absoluteElevation")));
            etShowWater.setText(String.valueOf(getArguments().getDouble("showWater")));
            etStayWater.setText(String.valueOf(getArguments().getDouble("stayWater")));
            // назначаем спинеру тип выработки
            String[] array = getResources().getStringArray(R.array.exc_array);
            for (int i = 0; i < array.length; i++) {
                if (array[i].equals(getArguments().getString("type"))) {
                    spTypeExc.setSelection(i);
                    break;
                }
            }
        }
        builder.setTitle(title);
        tvDateStart.setText(dateStart);
        tvDateEnd.setText(dateEnd);
        tvDateWaterStay.setText(dateWaterStay);
        tvDateWaterShow.setText(dateWaterShow);

        // вызываем диалог DatePicker
        tvDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempChoice = 1;
                DialogDatePicker changeDate = new DialogDatePicker();
                Bundle args = new Bundle();
                args.putString("date", dateStart);
                changeDate.setArguments(args);
                changeDate.show(getFragmentManager(), "changeDate");
            }
        });

        // вызываем диалог DatePicker
        tvDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempChoice = 2;
                DialogDatePicker changeDate = new DialogDatePicker();
                Bundle args = new Bundle();
                args.putString("date", dateEnd);
                changeDate.setArguments(args);
                changeDate.show(getFragmentManager(), "changeDate");
            }
        });

        // вызываем диалог DatePicker
        tvDateWaterStay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempChoice = 3;
                DialogDatePicker changeDate = new DialogDatePicker();
                Bundle args = new Bundle();
                args.putString("date", dateWaterStay);
                changeDate.setArguments(args);
                changeDate.show(getFragmentManager(), "changeDate");
            }
        });

        // вызываем диалог DatePicker
        tvDateWaterShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempChoice = 4;
                DialogDatePicker changeDate = new DialogDatePicker();
                Bundle args = new Bundle();
                args.putString("date", dateWaterShow);
                changeDate.setArguments(args);
                changeDate.show(getFragmentManager(), "changeDate");
            }
        });

        // открываем диалог выбора местоположения
        ibGetCoordinate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        builder.setView(view);
        if (editExc) {
            builder.setIcon(R.drawable.ic_edit_black_24dp);
        } else if (newExc) {
            builder.setIcon(R.drawable.ic_add_black_24dp);
        } else if (loadExc) {
            builder.setIcon(R.drawable.ic_file_download_black_24dp);
        } else if (copyExc) {
            builder.setIcon(R.drawable.ic_content_copy_black_24dp);
        }
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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
                    if (newExc) {
                        try {
                            if (testNewName()) {
                                // передаем новые значене и записываем их в базу
                                QueryProcessing.addNewExcavation(
                                        getArguments().getString("nameObject"),
                                        spTypeExc.getSelectedItem().toString(),
                                        etNumberExc.getText().toString(),
                                        tvDateStart.getText().toString(),
                                        tvDateEnd.getText().toString(),
                                        tvDateWaterStay.getText().toString(),
                                        tvDateWaterShow.getText().toString(),
                                        etLatitude.getText().toString(),
                                        etLongitude.getText().toString(),
                                        etDescriptionExcavation.getText().toString(),
                                        etWho.getText().toString(),
                                        etThan.getText().toString(),
                                        etHow.getText().toString(),
                                        getTestDouble(etShowWater),
                                        getTestDouble(etStayWater),
                                        getTestDouble(etAbsoluteElevation),
                                        getActivity());
                                Intent intent = new Intent("pressButtonOkInDialogExc");
                                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                                alertDialog.dismiss();
                            } else {
                                Toast.makeText(getContext(), R.string.nameExcTest, Toast.LENGTH_SHORT).show();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    if (editExc) {
                        try {
                            if (testEditName()) {
                                QueryProcessing.editExcavation(
                                        getArguments().getLong("idExc"),
                                        spTypeExc.getSelectedItem().toString(),
                                        etNumberExc.getText().toString(),
                                        tvDateStart.getText().toString(),
                                        tvDateEnd.getText().toString(),
                                        tvDateWaterStay.getText().toString(),
                                        tvDateWaterShow.getText().toString(),
                                        etLatitude.getText().toString(),
                                        etLongitude.getText().toString(),
                                        etDescriptionExcavation.getText().toString(),
                                        etWho.getText().toString(),
                                        etThan.getText().toString(),
                                        etHow.getText().toString(),
                                        getTestDouble(etShowWater),
                                        getTestDouble(etStayWater),
                                        getTestDouble(etAbsoluteElevation),
                                        getActivity());
                                Intent intent = new Intent("pressButtonOkInDialogExc");
                                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                                alertDialog.dismiss();
                            } else {
                                Toast.makeText(getContext(), R.string.nameExcTest, Toast.LENGTH_SHORT).show();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    if (loadExc || copyExc) {
                        try {
                            if (testNewName()) {
                                QueryProcessing.addNewExcavation(
                                        getArguments().getString("nameObject"),
                                        spTypeExc.getSelectedItem().toString(),
                                        etNumberExc.getText().toString(),
                                        tvDateStart.getText().toString(),
                                        tvDateEnd.getText().toString(),
                                        tvDateWaterStay.getText().toString(),
                                        tvDateWaterShow.getText().toString(),
                                        etLatitude.getText().toString(),
                                        etLongitude.getText().toString(),
                                        etDescriptionExcavation.getText().toString(),
                                        etWho.getText().toString(),
                                        etThan.getText().toString(),
                                        etHow.getText().toString(),
                                        getTestDouble(etShowWater),
                                        getTestDouble(etStayWater),
                                        getTestDouble(etAbsoluteElevation),
                                        getActivity());
                                Intent intent = new Intent("pressButtonOkInDialogExcLoadOrCopy");
                                if (copyExc) {
                                    intent.putExtra("COPY_EXC", true);
                                    intent.putExtra("ID_EXC", getArguments().getLong("idExc"));
                                } else {
                                    intent.putExtra("COPY_EXC", false);
                                }
                                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                                alertDialog.dismiss();
                            } else {
                                Toast.makeText(getContext(), R.string.nameExcTest, Toast.LENGTH_SHORT).show();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    // проверка на ввод абсолютной отметки
    private double getTestDouble(EditText editText) {
        if (editText.getText().toString().equals("")) {
            return 0;
        } else {
            return Double.parseDouble(editText.getText().toString());
        }
    }

    // проверяем тип и имя выработки на пусутую стороку и на одинаковость
    private Boolean testNewName() throws ParseException {
        boolean temp = false;
        if (list.size() > 0) {
            for (Excavation value : list) {
                if (value.getNameExcavation().equals(etNumberExc.getText().toString()) || etNumberExc.getText().toString().trim().isEmpty()
                        || !testDate(tvDateStart.getText().toString(), tvDateEnd.getText().toString()) || !CheckString.isFileNameCorrect(etNumberExc.getText().toString())) {
                    temp = false;
                    break;
                } else {
                    temp = true;
                }
            }
        } else {
            if (etNumberExc.getText().toString().trim().isEmpty() || !testDate(tvDateStart.getText().toString(), tvDateEnd.getText().toString()) || !CheckString.isFileNameCorrect(etNumberExc.getText().toString())) {
                temp = false;
            } else {
                temp = true;
            }
        }
        return temp;
    }


    // проверяем тип и имя выработки на пусутую стороку и на одинаковость
    private Boolean testEditName() throws ParseException {
        boolean temp = false;
        for (Excavation value : list) {
            if (getArguments().getString("number").equals(etNumberExc.getText().toString()) && testDate(tvDateStart.getText().toString(), tvDateEnd.getText().toString())) {
                temp = true;
                break;
            } else if (value.getNameExcavation().equals(etNumberExc.getText().toString())) {
                temp = false;
                break;
            } else if (!CheckString.isFileNameCorrect(etNumberExc.getText().toString())) {
                temp = false;
                break;
            } else if (etNumberExc.getText().toString().trim().isEmpty()) {
                temp = false;
                break;
            } else if (!testDate(tvDateStart.getText().toString(), tvDateEnd.getText().toString())) {
                temp = false;
                break;
            } else {
                temp = true;
            }
        }
        return temp;
    }

    private boolean testDate(String dateStart, String dateEnd) throws ParseException {
        return MyDateUtils.datesComparing(MyDateUtils.convertStringToDate(dateStart), MyDateUtils.convertStringToDate(dateEnd));
    }

    // получаем значения координат с окна выбора местоположения
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            Place place = PlacePicker.getPlace(data, getContext());
            etLatitude.setText(String.valueOf(place.getLatLng().latitude));
            etLongitude.setText(String.valueOf(place.getLatLng().longitude));
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (tempChoice == 1) {
                tvDateStart.setText(intent.getStringExtra("returnDate"));
            } else if (tempChoice == 2) {
                tvDateEnd.setText(intent.getStringExtra("returnDate"));
            } else if (tempChoice == 3) {
                tvDateWaterStay.setText(intent.getStringExtra("returnDate"));
            } else if (tempChoice == 4) {
                tvDateWaterShow.setText(intent.getStringExtra("returnDate"));
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
