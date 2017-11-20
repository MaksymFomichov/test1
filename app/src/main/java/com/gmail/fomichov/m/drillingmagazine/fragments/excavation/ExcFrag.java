package com.gmail.fomichov.m.drillingmagazine.fragments.excavation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.gmail.fomichov.m.drillingmagazine.MainActivity;
import com.gmail.fomichov.m.drillingmagazine.R;
import com.gmail.fomichov.m.drillingmagazine.database.QueryProcessing;
import com.gmail.fomichov.m.drillingmagazine.dialogs.DialogExc;
import com.gmail.fomichov.m.drillingmagazine.geology.Excavation;
import com.gmail.fomichov.m.drillingmagazine.geology.Layer;
import com.gmail.fomichov.m.drillingmagazine.geology.Probe;
import com.gmail.fomichov.m.drillingmagazine.utils.JsonGenerate;
import com.gmail.fomichov.m.drillingmagazine.utils.MyLog;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Pattern;


public class ExcFrag extends Fragment {
    public static List<Excavation> list;
    private static Context context = null;
    private static String nameObject;
    public static BottomNavigationView bottomNavigationView;
    private static boolean loadExc; // если true то значит мы только что загрузили скважину
    private Excavation newExc;
    private static final int REQUEST_CODE_JSON_LOAD = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true); // без этого меню не показывается
        context = getActivity();
        nameObject = getArguments().getString("name");
        getActivity().setTitle(getString(R.string.title_exc_frag) + " - " + nameObject);
        list = QueryProcessing.getListExcavation(context, nameObject);
        View view = inflater.inflate(R.layout.frag_exc, container, false);
        bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.nav_exc_list:
                                selectedFragment = ExcFragList.newInstance(nameObject);
                                break;
                            case R.id.nav_exc_map:
                                selectedFragment = ExcFragMap.newInstance(nameObject);
                                break;
//                            case R.id.nav_exc_stat:
//                                selectedFragment = ExcFragStat.newInstance();
//                                break;
                        }
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, ExcFragList.newInstance(nameObject));
        transaction.commit();
        return view;
    }

    public static void updateList() {
        list.clear();
        list = QueryProcessing.getListExcavation(context, nameObject);
        MyLog.showLog("updateList");
    }

    // показываем кнопку загрузки в верхней панели
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        inflater.inflate(R.menu.menu_exc, menu);
        menu.findItem(R.id.menuDownload).setIcon(getResources().getDrawable(R.drawable.ic_file_download_white_24dp));
    }

    // обрабатываем нажатие на кнопку загрузки файла выработки
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuDownload:
                // запускаем диалог выбора файла
                Intent intent = new Intent(getContext(), FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.ARG_FILTER, Pattern.compile(".*\\.exc$"));
                intent.putExtra(FilePickerActivity.ARG_CLOSEABLE, true);
                startActivityForResult(intent, REQUEST_CODE_JSON_LOAD);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // получаем файл и передаем его для парсинга
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_JSON_LOAD && resultCode == Activity.RESULT_OK) {
            startLoadExc(new JsonGenerate().getJSON(data));
        }
    }

    // парсим файл и передаем его в диалог выработки
    private void startLoadExc(String json) {
        newExc = JSON.parseObject(json, Excavation.class);
        showLoadExcavationObject(
                getResources().getString(R.string.title_load_exc_dialog), newExc.getDateStart(), newExc.getDateEnd(), newExc.getDateWaterStay(), newExc.getDateWaterShow(), newExc.getLatitude(), newExc.getLongitude(), newExc.getDescriptionExcavation(),
                newExc.getWhoWorked(), newExc.getEquipment(), newExc.getPenetrationMethod(), newExc.getNameExcavation(), newExc.getTypeExcavation(), nameObject, newExc.getAbsoluteElevation(),
                newExc.getWaterShow(), newExc.getWaterStay());
    }

    // показываем диалог загрузки выработки
    public void showLoadExcavationObject(String title, String dateStart, String dateEnd, String dateWaterStay, String dateWaterShow, String latitude, String longitude, String description,
                                         String who, String than, String how, String number, String type, String nameObject, double absoluteElevation, double showWater, double stayWater) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        DialogFragment newFragment = DialogExc.newInstance(title, dateStart, dateEnd, dateWaterStay, dateWaterShow, latitude, longitude, description, who, than, how, number, type, nameObject, absoluteElevation, showWater, stayWater);
        newFragment.show(fragmentTransaction, "dialog");
        loadExc = true;
    }

    // ловим нажатие ок в диалоге, в зависимости от булеана COPY_EXC выполняется действия
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            MyLog.showLog("pressButtonOkInDialogExcLoadOrCopy");
            if (intent.getExtras().getBoolean("COPY_EXC")) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getResources().getString((R.string.msgProgressDialog)));
                progressDialog.show();
                final List<Layer> layerList = QueryProcessing.getListLayer(context, intent.getExtras().getLong("ID_EXC"));
                final List<Probe> probeList = QueryProcessing.getListProbe(context, intent.getExtras().getLong("ID_EXC"));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // получаем id загруженной выработки
                        long newExcId = QueryProcessing.getNewExcavationId(context);
                        int countLayer = layerList.size();
                        int countProbe = probeList.size();
                        for (int i = 0; i < countLayer; i++) {
                            QueryProcessing.addNewLayer(
                                    nameObject,
                                    layerList.get(i).getStartDepth(),
                                    layerList.get(i).getEndDepth(),
                                    layerList.get(i).getDescription(),
                                    newExcId,
                                    getActivity());
                        }
                        for (int i = 0; i < countProbe; i++) {
                            QueryProcessing.addNewProbe(
                                    nameObject,
                                    probeList.get(i).getDepth(),
                                    probeList.get(i).getDescription(),
                                    newExcId,
                                    probeList.get(i).getType(),
                                    getActivity());
                        }
                        progressDialog.dismiss();
                    }
                }).start();
                ExcFragList.updateRecyclerListExc();
                MyLog.showLog("here " + intent.getExtras().getLong("ID_EXC"));
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // получаем id загруженной выработки
                        long newExcId = QueryProcessing.getNewExcavationId(context);
                        int countLayer = newExc.getLayers().size();
                        int countProbe = newExc.getProbes().size();
                        for (int i = 0; i < countLayer; i++) {
                            QueryProcessing.addNewLayer(
                                    nameObject,
                                    newExc.getLayers().get(i).getStartDepth(),
                                    newExc.getLayers().get(i).getEndDepth(),
                                    newExc.getLayers().get(i).getDescription(),
                                    newExcId,
                                    getActivity());
                        }
                        for (int i = 0; i < countProbe; i++) {
                            QueryProcessing.addNewProbe(
                                    nameObject,
                                    newExc.getProbes().get(i).getDepth(),
                                    newExc.getProbes().get(i).getDescription(),
                                    newExcId,
                                    newExc.getProbes().get(i).getType(),
                                    getActivity());
                        }
                    }
                }).start();
                ExcFragList.updateRecyclerListExc();
            }
        }
    };

    // когда возращаемся на фрагмент обьектов
    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.showLog("onDestroy");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    // когда возращаемся из фрагментов слоев и проб
    @Override
    public void onPause() {
        super.onPause();
        MyLog.showLog("onPause");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.showLog("onResume");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("pressButtonOkInDialogExcLoadOrCopy"));
    }
}
