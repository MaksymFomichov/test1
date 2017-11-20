package com.gmail.fomichov.m.drillingmagazine.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gmail.fomichov.m.drillingmagazine.MainActivity;
import com.gmail.fomichov.m.drillingmagazine.R;
import com.gmail.fomichov.m.drillingmagazine.adapters.RecyclerExcavationAdapter;
import com.gmail.fomichov.m.drillingmagazine.adapters.RecyclerObjectsAdapter;
import com.gmail.fomichov.m.drillingmagazine.database.QueryProcessing;
import com.gmail.fomichov.m.drillingmagazine.dialogs.DialogAbout;
import com.gmail.fomichov.m.drillingmagazine.dialogs.DialogDelete;
import com.gmail.fomichov.m.drillingmagazine.dialogs.DialogDownloadBaseFromDrive;
import com.gmail.fomichov.m.drillingmagazine.dialogs.DialogObj;
import com.gmail.fomichov.m.drillingmagazine.drive.DriveOpenFile;
import com.gmail.fomichov.m.drillingmagazine.drive.DriveUploadFileInFolder;
import com.gmail.fomichov.m.drillingmagazine.fragments.excavation.ExcFrag;
import com.gmail.fomichov.m.drillingmagazine.geology.Excavation;
import com.gmail.fomichov.m.drillingmagazine.geology.ObjectGeology;
import com.gmail.fomichov.m.drillingmagazine.utils.JsonGenerate;
import com.gmail.fomichov.m.drillingmagazine.utils.MyLog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.List;
import java.util.regex.Pattern;

public class ObjFragList extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static View view;
    private RecyclerView recyclerView;
    private static RecyclerObjectsAdapter adapter;
    private static List<ObjectGeology> objectGeologyList;
    private ObjectGeology loadObj;
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_CODE_JSON_LOAD = 1;
    private static final int REQUEST_CODE_RESOLUTION = 2;
    private static final int REQUEST_CODE_OPENER = 3;
    private int keySync;
    private DriveId mSelectedFileDriveId;
    private static Context context = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true); // без этого меню не показывается
        getActivity().setTitle(R.string.title_object_fragment);
        context = getActivity();
        view = inflater.inflate(R.layout.frag_list_obj, container, false);
        // при перовм запуске затребуем регистрацию в гугл драйв
        if (!MainActivity.mSettings.contains(DriveUploadFileInFolder.APP_PREFERENCES_CREATE_FOLDER)) {
            startGoogleApiClient();
        }
        // передаем адаптеру данные и запускаем просмотр
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        objectGeologyList = QueryProcessing.getListObjects(getActivity());
        adapter = new RecyclerObjectsAdapter(objectGeologyList, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);

        adapter.setOnItemClickListener(new RecyclerObjectsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position, View view) {
                switch (view.getId()) {
                    case R.id.recyclerObjectLinearLayout:
                        Bundle bundle = new Bundle();
                        bundle.putString("name", objectGeologyList.get(position).getName());
                        FragmentTransaction fTransaction = getFragmentManager().beginTransaction();
                        ExcFrag fragment = new ExcFrag();
                        fragment.setArguments(bundle);
                        fTransaction.replace(R.id.container, fragment);
                        fTransaction.addToBackStack(null); // запущенный фрагмент по кнопке назад верется на этот
                        fTransaction.commit();
                        break;
                }
            }
        });

        // долгое нажатие на элемент списка
        adapter.setOnLongClickListener(new RecyclerExcavationAdapter.OnLongClickListener() {
            @Override
            public void onItemClick(View itemView, int position, View view) {
                showPopupMenu(view, position);
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        // управление FloatingActionButton скрытиие и появление при прокручивание списка
        NestedScrollView nsv = (NestedScrollView) view.findViewById(R.id.nsv);
        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });

        // запускаем диалог создания обьекта и передаем туда текущую дату и имя заголовка
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewDialogObject(getString(R.string.title_new_obj_dialog));
            }
        });
        return view;
    }

    // показываем диалог создания обьекта
    private void showNewDialogObject(String title) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        DialogFragment newFragment = DialogObj.newInstance(title);
        newFragment.show(fragmentTransaction, "dialog");
    }

    // показываем диалог редактирования обьекта
    private void showEditDialogObject(String title, String name, String description) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        DialogFragment newFragment = DialogObj.newInstance(title, name, description);
        newFragment.show(fragmentTransaction, "dialog");
    }

    // показываем диалог удаления обьекта
    private void showDeleteDialogObject(int typeObjectBD, String message, String name, String type, String toast) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        DialogFragment newFragment = DialogDelete.newInstance(typeObjectBD, message, name, type, toast);
        newFragment.show(fragmentTransaction, "dialog");
    }

    // метод определяет надобность показа стартовой надписи если нет ниодного обьекта
    private static void showStartMessage() {
        TextView tvStartHelpMessage = (TextView) view.findViewById(R.id.tvStartHelpMessage);
        if (objectGeologyList.size() != 0) {
            tvStartHelpMessage.setVisibility(View.GONE);
        } else {
            tvStartHelpMessage.setVisibility(View.VISIBLE);
        }
    }

    // контекстное меню, появляется при долгом нажатии на обьект
    public void showPopupMenu(View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.menu_popup_obj);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.popupEdit:
                        showEditDialogObject(getString(R.string.title_edit_obj_dialog), objectGeologyList.get(position).getName(), objectGeologyList.get(position).getDescription());
                        return true;
                    case R.id.popupSave:
                        new JsonGenerate().writeFileObj(objectGeologyList.get(position).getName(), getContext(), getActivity(), objectGeologyList.get(position).getDescription());
                        return true;
                    case R.id.popupDelete:
                        showDeleteDialogObject(1, getString(R.string.message_delete_obj), objectGeologyList.get(position).getName(), getString(R.string.delete_obj), getString(R.string.delete_obj_toast));
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });
        popupMenu.show();
    }

    // подключаем меню в статус баре
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        inflater.inflate(R.menu.menu_obj, menu);
        menu.findItem(R.id.menuDownload).setIcon(getResources().getDrawable(R.drawable.ic_file_download_white_24dp));
        menu.findItem(R.id.menuUploadFileToDrive).setIcon(getResources().getDrawable(R.drawable.ic_cloud_upload_white_24dp));
    }

    // обрабатываем нажатия на элементы меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menuDownload: // загружаем файл обьекта
                Intent intent = new Intent(getContext(), FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.ARG_FILTER, Pattern.compile(".*\\.obj$"));
                intent.putExtra(FilePickerActivity.ARG_CLOSEABLE, true);
                startActivityForResult(intent, REQUEST_CODE_JSON_LOAD);
                break;
            case R.id.menuUploadFileToDrive: // выгружаем полную копию базы данных в облаако
                keySync = 1;
                startGoogleApiClient();
                break;
            case R.id.action_help:
                if (objectGeologyList.size() == 0) {
                    keySync = 2;
                    startGoogleApiClient();
                } else {
                    AlertDialog dialog = DialogDownloadBaseFromDrive.getDialog(getActivity(), getContext());
                    dialog.show();
                }
                break;
            case R.id.action_payBeer:

                break;
            case R.id.action_star:

                break;
            case R.id.action_about:
                AlertDialog dialog = DialogAbout.getDialog(getActivity());
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // показываем диалог загрузки обьекта
    private void showLoadDialogObject(String title, String name, String description) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        DialogFragment newFragment = DialogObj.newInstanceLoad(title, name, description);
        newFragment.show(fragmentTransaction, "dialog");
    }

    // парсим файл и передаем его в диалог выработки
    private void startLoadObj(String json) {
        loadObj = JSON.parseObject(json, ObjectGeology.class);
        showLoadDialogObject(getResources().getString(R.string.title_load_obj_dialog), loadObj.getName(), loadObj.getDescription());
    }

    // region BroadcastReceiver - действия после нажатия кнопки ок в диалоге обьекта
    // действия после нажатия кнопки ок в диалоге обьекта
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            MyLog.showLog("pressButtonOkInDialogObj");
            if (intent.getExtras().getBoolean("LOAD_OBJ")) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getResources().getString((R.string.msgProgressDialog)));
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // получаем id загруженной выработки
                        String newNameObj = intent.getExtras().getString("NAME_OBJ");
                        int countExcavation = loadObj.getExcavations().size();
                        for (int i = 0; i < countExcavation; i++) {
                            Excavation tempExc = loadObj.getExcavations().get(i);
                            QueryProcessing.addNewExcavation(
                                    newNameObj,
                                    tempExc.getTypeExcavation(),
                                    tempExc.getNameExcavation(),
                                    tempExc.getDateStart(),
                                    tempExc.getDateEnd(),
                                    tempExc.getDateWaterStay(),
                                    tempExc.getDateWaterShow(),
                                    tempExc.getLatitude(),
                                    tempExc.getLongitude(),
                                    tempExc.getDescriptionExcavation(),
                                    tempExc.getWhoWorked(),
                                    tempExc.getEquipment(),
                                    tempExc.getPenetrationMethod(),
                                    tempExc.getWaterShow(),
                                    tempExc.getWaterStay(),
                                    tempExc.getAbsoluteElevation(),
                                    context);
                            long newExcId = QueryProcessing.getNewExcavationId(context);
                            int countLayer = tempExc.getLayers().size();
                            for (int j = 0; j < countLayer; j++) {
                                QueryProcessing.addNewLayer(
                                        newNameObj,
                                        tempExc.getLayers().get(j).getStartDepth(),
                                        tempExc.getLayers().get(j).getEndDepth(),
                                        tempExc.getLayers().get(j).getDescription(),
                                        newExcId,
                                        getActivity());
                            }
                            int countProbe = tempExc.getProbes().size();
                            for (int k = 0; k < countProbe; k++) {
                                QueryProcessing.addNewProbe(
                                        newNameObj,
                                        tempExc.getProbes().get(k).getDepth(),
                                        tempExc.getProbes().get(k).getDescription(),
                                        newExcId,
                                        tempExc.getProbes().get(k).getType(),
                                        getActivity());
                            }
                        }
                        progressDialog.dismiss();
                    }
                }).start();
            } else if (intent.getExtras().getBoolean("LOAD_DRIVE")) {
                // удаление всех обьектов из базы
                deleteAllObjects();
            }
            objectGeologyList.clear();
            objectGeologyList = QueryProcessing.getListObjects(getActivity());
            adapter.setListItems(objectGeologyList);
            adapter.notifyDataSetChanged();
            showStartMessage();
        }
    };
    // endregion

    // удаляем все обьекты если пользователь подвердил, что он будет загружать базу из облака
    private void deleteAllObjects() {
        for (ObjectGeology value : objectGeologyList) {
            QueryProcessing.deleteObject(value.getName(), getContext());
        }
        keySync = 2;
        startGoogleApiClient();
    }

    // запускаем апи гугл драйва, если он еще не бы создан то вылезет окно с выбором аккаунта
    private void startGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER) // разрешение
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    // ловим закртыия разных диалогов
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_JSON_LOAD: // загрука обьета
                if (resultCode == Activity.RESULT_OK) {
                    startLoadObj(new JsonGenerate().getJSON(data));
                }
                break;
            case REQUEST_CODE_OPENER: // загрузка базы из гугл драйв
                if (resultCode == MainActivity.RESULT_OK) {
                    mSelectedFileDriveId = (DriveId) data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    disconnectGoogleApiClient();
                    MyLog.showLog("Выбранный файл ID: " + mSelectedFileDriveId);
                    keySync = 3;
                    startGoogleApiClient();
                }
                break;
        }
    }

    // апи клиен запущен
    @Override
    public void onConnected(Bundle connectionHint) {
        MyLog.showLog("API-клиент подключен");
        if (keySync == 1) {
            DriveUploadFileInFolder driveUploadFileInFolder = new DriveUploadFileInFolder(mGoogleApiClient, getContext());
            driveUploadFileInFolder.saveFileToDrive();
        } else if (keySync == 2) { // запускаем открытие диалога выбора файла с гугл доайв, после удаления всех обьектов
            openDialogChoiceFileFromDrive();
        } else if (keySync == 3) { // запуск загрузка файла из диска
            if (mSelectedFileDriveId != null) {
                DriveOpenFile driveOpenFile = new DriveOpenFile(mSelectedFileDriveId, mGoogleApiClient, getContext());
                driveOpenFile.open();
                return;
            }
        }
    }

    // запускаем окно выбора файла с гугл диска
    private void openDialogChoiceFileFromDrive() {
        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{"text/plain"})
                .build(mGoogleApiClient);
        try {
            startIntentSenderForResult(intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0, new Bundle());
        } catch (IntentSender.SendIntentException e) {
            MyLog.showLog("Не удалось отправить намерение " + e);
        }
    }

    private void disconnectGoogleApiClient() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            MyLog.showLog("API-клиент отключен");
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        MyLog.showLog("Соединение GoogleApiClient приостановлено");
    }

    // Вызывается всякий раз, когда клиент API не удается подключиться.
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Toast.makeText(getContext(), R.string.dontConnectGoogleApiClient, Toast.LENGTH_SHORT).show();
        if (!result.hasResolution()) {
            // показать локализованное диалоговое окно ошибки.
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), result.getErrorCode(), 0).show();
            return;
        }
        // Ошибка имеет разрешение. Разрешите это.
        // Вызывается обычно, когда приложение еще не разрешено,
        // и пользователю отображается диалоговое окно автосогласования.
        try {
            result.startResolutionForResult(getActivity(), REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            MyLog.showLog("Исключение при запуске действия разрешения " + e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        showStartMessage();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("pressButtonOkInDialogObj"));
        showStartMessage();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        showStartMessage();
    }

    public static void updateRecyclerView() {
        objectGeologyList = QueryProcessing.getListObjects(context);
        adapter.setListItems(objectGeologyList);
        adapter.notifyDataSetChanged();
        showStartMessage();
        Toast.makeText(context, R.string.goodLoadDataBaseFromDrive, Toast.LENGTH_SHORT).show();
    }
}
