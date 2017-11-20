package com.gmail.fomichov.m.drillingmagazine.fragments.excavation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.gmail.fomichov.m.drillingmagazine.R;
import com.gmail.fomichov.m.drillingmagazine.adapters.RecyclerExcavationAdapter;
import com.gmail.fomichov.m.drillingmagazine.dialogs.DialogDelete;
import com.gmail.fomichov.m.drillingmagazine.dialogs.DialogExc;
import com.gmail.fomichov.m.drillingmagazine.fragments.layer_probe.LayerProbeFrag;
import com.gmail.fomichov.m.drillingmagazine.utils.JsonGenerate;
import com.gmail.fomichov.m.drillingmagazine.utils.MyDateUtils;
import com.gmail.fomichov.m.drillingmagazine.utils.MyLog;

public class ExcFragList extends Fragment {
    private static final String NAME_OBJECT = "name_object";
    private View view;
    private RecyclerView recyclerView;
    private static RecyclerExcavationAdapter adapter;
    private static FloatingActionButton fab;
    private String nameObject;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        nameObject = getArguments().getString("name_object");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("pressButtonOkInDialogExc"));
        view = inflater.inflate(R.layout.frag_list_exc, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecyclerExcavationAdapter(ExcFrag.list, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        adapter.setOnItemClickListener(new RecyclerExcavationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position, View view) {
                switch (view.getId()) {
                    case R.id.recyclerExcavationLinearLayout:
                        Bundle bundle = new Bundle();
                        bundle.putString("nameObj", nameObject);
                        bundle.putString("nameExc", ExcFrag.list.get(position).getNameExcavation());
                        bundle.putString("typeExc", ExcFrag.list.get(position).getTypeExcavation());
                        bundle.putDouble("absoluteElevation", ExcFrag.list.get(position).getAbsoluteElevation());
                        bundle.putLong("keyExc", ExcFrag.list.get(position).getKeyExc());
                        FragmentTransaction fTransaction = getFragmentManager().beginTransaction();
                        LayerProbeFrag fragment = new LayerProbeFrag();
                        fragment.setArguments(bundle);
                        fTransaction.replace(R.id.container, fragment);
                        fTransaction.addToBackStack(null); // запущенный фрагмент по кнопке назад верется на этот
                        fTransaction.commit();
                        break;
                }
            }
        });

        adapter.setOnLongClickListener(new RecyclerExcavationAdapter.OnLongClickListener() {
            @Override
            public void onItemClick(View itemView, int position, View view) {
                showPopupMenu(view, position);
            }
        });

        // заупускаем диалог создания обьекта и передаем туда текущую дату и имя заголовка
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewExcavationObject(getString(R.string.title_new_exc_dialog), MyDateUtils.getCurrentDate(), getArguments().getString("name_object"));
            }
        });
        return view;
    }

    public void showPopupMenu(View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.menu_popup_exc);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.popupEdit:
                        showEditExcavationObject(
                                ExcFrag.list.get(position).getKeyExc(),
                                getString(R.string.title_edit_exc_dialog),
                                ExcFrag.list.get(position).getDateStart(),
                                ExcFrag.list.get(position).getDateEnd(),
                                ExcFrag.list.get(position).getDateWaterStay(),
                                ExcFrag.list.get(position).getDateWaterShow(),
                                ExcFrag.list.get(position).getLatitude(),
                                ExcFrag.list.get(position).getLongitude(),
                                ExcFrag.list.get(position).getDescriptionExcavation(),
                                ExcFrag.list.get(position).getWhoWorked(),
                                ExcFrag.list.get(position).getEquipment(),
                                ExcFrag.list.get(position).getPenetrationMethod(),
                                ExcFrag.list.get(position).getNameExcavation(),
                                ExcFrag.list.get(position).getTypeExcavation(),
                                nameObject,
                                ExcFrag.list.get(position).getAbsoluteElevation(),
                                ExcFrag.list.get(position).getWaterShow(),
                                ExcFrag.list.get(position).getWaterStay());
                        return true;
                    case R.id.popupCopy:
                        showCopyExcavationObject(
                                ExcFrag.list.get(position).getKeyExc(),
                                getString(R.string.title_copy_exc_dialog),
                                ExcFrag.list.get(position).getDateStart(),
                                ExcFrag.list.get(position).getDateEnd(),
                                ExcFrag.list.get(position).getDateWaterStay(),
                                ExcFrag.list.get(position).getDateWaterShow(),
                                ExcFrag.list.get(position).getLatitude(),
                                ExcFrag.list.get(position).getLongitude(),
                                ExcFrag.list.get(position).getDescriptionExcavation(),
                                ExcFrag.list.get(position).getWhoWorked(),
                                ExcFrag.list.get(position).getEquipment(),
                                ExcFrag.list.get(position).getPenetrationMethod(),
                                ExcFrag.list.get(position).getNameExcavation(),
                                ExcFrag.list.get(position).getTypeExcavation(),
                                nameObject,
                                ExcFrag.list.get(position).getAbsoluteElevation(),
                                ExcFrag.list.get(position).getWaterShow(),
                                ExcFrag.list.get(position).getWaterStay());
                        return true;
                    case R.id.popupSave:
                        new JsonGenerate().writeFileExc(position, nameObject, getContext(), getActivity());
                        return true;
                    case R.id.popupSavePDF:
                        new JsonGenerate().createFilePdfExc(position, nameObject, getContext(), getActivity());
                        return true;
                    case R.id.popupDelete:
                        // 2 - это обозначение выработки
                        showDeleteDialogObject(
                                2,
                                getString(R.string.message_delete_exc),
                                ExcFrag.list.get(position).getTypeExcavation(),
                                ExcFrag.list.get(position).getNameExcavation(),
                                getString(R.string.delete_exc),
                                getString(R.string.delete_exc_toast),
                                ExcFrag.list.get(position).getKeyExc());
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

    // показываем диалог  удаления обьекта
    private void showDeleteDialogObject(int typeObjectBD, String message, String typeExcavation, String name, String type, String toast, long idExc) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        DialogFragment newFragment = DialogDelete.newInstance(typeObjectBD, message, typeExcavation, name, type, toast, idExc);
        newFragment.show(fragmentTransaction, "dialog");
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyLog.showLog("pressButtonOkInDialogExc");
            updateRecyclerListExc();
            showStartMessage();
        }
    };

    public static void updateRecyclerListExc() {
        ExcFrag.list.clear();
        ExcFrag.updateList();
        adapter.setListItems(ExcFrag.list);
        adapter.notifyDataSetChanged();
    }

    // показываем диалог создания выработки
    private void showNewExcavationObject(String title, String date, String nameObject) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        DialogFragment newFragment = DialogExc.newInstance(title, date, nameObject);
        newFragment.show(fragmentTransaction, "dialog");
    }

    // показываем диалог редактирования выработки
    private void showEditExcavationObject(long idExc, String title, String dateStart, String dateEnd, String dateWaterStay, String dateWaterShow, String latitude, String longitude, String description,
                                          String who, String than, String how, String number, String type, String nameObject, double absoluteElevation, double showWater, double stayWater) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        DialogFragment newFragment = DialogExc.newInstance(idExc, title, dateStart, dateEnd, dateWaterStay, dateWaterShow, latitude, longitude, description, who, than, how, number, type, nameObject, absoluteElevation, showWater, stayWater);
        newFragment.show(fragmentTransaction, "dialog");
    }

    // показываем диалог копирования выработки
    private void showCopyExcavationObject(long idExc, String title, String dateStart, String dateEnd, String dateWaterStay, String dateWaterShow, String latitude, String longitude, String description,
                                          String who, String than, String how, String number, String type, String nameObject, double absoluteElevation, double showWater, double stayWater) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        DialogFragment newFragment = DialogExc.newInstance(title, idExc, dateStart, dateEnd, dateWaterStay, dateWaterShow, latitude, longitude, description, who, than, how, number, type, nameObject, absoluteElevation, showWater, stayWater);
        newFragment.show(fragmentTransaction, "dialog");
    }

    // получаем имя обьекта
    public static ExcFragList newInstance(String nameObject) {
        ExcFragList fragment = new ExcFragList();
        Bundle args = new Bundle();
        args.putString(NAME_OBJECT, nameObject);
        fragment.setArguments(args);
        return fragment;
    }

    private void showStartMessage() {
        TextView tvStartHelpMessage = (TextView) view.findViewById(R.id.tvStartHelpMessageExcavation);
        if (ExcFrag.list.size() != 0) {
            tvStartHelpMessage.setVisibility(View.GONE);
        } else {
            tvStartHelpMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showStartMessage();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        showStartMessage();
    }
}
