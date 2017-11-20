package com.gmail.fomichov.m.drillingmagazine.fragments.layer_probe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.fomichov.m.drillingmagazine.R;
import com.gmail.fomichov.m.drillingmagazine.adapters.RecyclerLayerAdapter;
import com.gmail.fomichov.m.drillingmagazine.database.QueryProcessing;
import com.gmail.fomichov.m.drillingmagazine.dialogs.DialogDelete;
import com.gmail.fomichov.m.drillingmagazine.dialogs.DialogLayer;
import com.gmail.fomichov.m.drillingmagazine.geology.Layer;
import com.gmail.fomichov.m.drillingmagazine.utils.MyLog;

import java.util.Arrays;
import java.util.List;

public class LayerFragList extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private RecyclerLayerAdapter adapter;
    private static final String NAME_OBJECT = "name_object";
    private static final String NUMBER_EXC = "number_exc";
    private static final String ABSOLUTE_ELEVATION = "absoluteElevation";
    private static final String ID_EXCAVATION = "keyExc";
    public static List<Layer> list;
    private static String nameObject, numberExc;
    private static long idExc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("pressButtonOkInDialogLayer"));
        nameObject = getArguments().getString("name_object");
        numberExc = getArguments().getString("number_exc");
        idExc = getArguments().getLong("keyExc");
        list = QueryProcessing.getListLayer(getContext(), idExc);

        view = inflater.inflate(R.layout.frag_list_layer, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // включаем динамический скролл, иначе будет, как деревянный
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new RecyclerLayerAdapter(list, getContext(), getArguments().getDouble("absoluteElevation"));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RecyclerLayerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position, View view) {
                switch (view.getId()) {
                    case R.id.ivEdit:
                        editLayerDialog(
                                list.get(position).getIdLayer(),
                                getString(R.string.title_edit_layer_dialog),
                                list.get(position).getDescription(),
                                list.get(position).getStartDepth(),
                                list.get(position).getEndDepth());
                        break;
                    case R.id.ivDelete:
                        // 4 - это обозначение слоя
                        showDeleteLayerDialog(
                                list.get(position).getStartDepth(),
                                list.get(position).getEndDepth(),
                                list.get(position).getIdLayer(),
                                getString(R.string.delete_layer),
                                getString(R.string.delete_layer_toast),
                                getString(R.string.message_delete_layer),
                                4);
                        break;
                }
            }
        });

        // прячем fab когда начинаем скролить список
        NestedScrollView nsv = (NestedScrollView) view.findViewById(R.id.nsv);
        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    LayerProbeFrag.getFloatingActionButton().hide();
                } else {
                    LayerProbeFrag.getFloatingActionButton().show();
                }
            }
        });

        return view;
    }

    // ловим закрытие диалоговых окон по кнопке ОК и обновляем лист
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyLog.showLog("pressButtonOkInDialogLayer");
            list.clear();
            list = QueryProcessing.getListLayer(getContext(), idExc);
            adapter.setListItems(list);
            adapter.notifyDataSetChanged();
            showStartMessage();
        }
    };

    // показывааем диалог редактирования слоя
    private void editLayerDialog(long idLayer, String title, String description, double startDepth, double endDepth) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        DialogFragment newFragment = DialogLayer.newInstance(idLayer, title, description, startDepth, endDepth);
        newFragment.show(fragmentTransaction, "dialog");
    }

    // показываем диалог удаления слоя
    private void showDeleteLayerDialog(double depthStartLayer, double depthEndLayer, long idLayer, String type, String toast, String message, int typeObjectBD) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        DialogFragment newFragment = DialogDelete.newInstance(depthStartLayer, depthEndLayer, idLayer, type, toast, typeObjectBD, message);
        newFragment.show(fragmentTransaction, "dialog");
    }

    // получаем начальные параметры исходя из выбранной выработки
    public static LayerFragList newInstance(String nameObject, String numberExc, double absoluteElevation, long keyExc) {
        LayerFragList fragment = new LayerFragList();
        Bundle args = new Bundle();
        args.putString(NAME_OBJECT, nameObject);
        args.putString(NUMBER_EXC, numberExc);
        args.putDouble(ABSOLUTE_ELEVATION, absoluteElevation);
        args.putLong(ID_EXCAVATION, keyExc);
        fragment.setArguments(args);
        return fragment;
    }

    // показываем или нет стартовое сообщенеи на весь экран, если нет слоев то показывется, если есть то не показывается
    private void showStartMessage() {
        TextView tvStartHelpMessage = (TextView) view.findViewById(R.id.tvStartHelpMessageLayer);
        if (list.size() != 0) {
            tvStartHelpMessage.setVisibility(View.GONE);
        } else {
            tvStartHelpMessage.setVisibility(View.VISIBLE);
        }
    }

    // перезапускаем проверку показа стартового сообщения, если этого не сделать, то при удалении последнегго элемента
    // в списке, стартовоесообщенени не будет показано
    @Override
    public void onResume() {
        super.onResume();
        showStartMessage();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }
}
