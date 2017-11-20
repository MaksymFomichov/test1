package com.gmail.fomichov.m.drillingmagazine.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.DatePicker;

import com.gmail.fomichov.m.drillingmagazine.utils.MyDateUtils;

import java.text.ParseException;
import java.util.Calendar;

public class DialogDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        try {
            c.setTime(MyDateUtils.dateFormat.parse(getArguments().getString("date")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Intent intent = new Intent("pressButtonOkInDialogDate");
        intent.putExtra("returnDate", checkDigit(dayOfMonth) + "." + checkDigit(month + 1) + "." + year);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    // добавляет ноль перед цифрой, если она одна
    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }
}
