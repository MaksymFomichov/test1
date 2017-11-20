package com.gmail.fomichov.m.drillingmagazine.fragments.layer_probe;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.fomichov.m.drillingmagazine.R;
import com.gmail.fomichov.m.drillingmagazine.adapters.LayerProbePagerAdapter;
import com.gmail.fomichov.m.drillingmagazine.dialogs.DialogLayer;
import com.gmail.fomichov.m.drillingmagazine.dialogs.DialogProbe;
import com.gmail.fomichov.m.drillingmagazine.utils.MyLog;

/**
 * Этот класс получает в себя имя обьектта, номер и тип выработки.
 * Запускает ViewPager и TabLayout для фрагментов ProbeFragList и LayerFragList
 */

public class LayerProbeFrag extends Fragment {
    private static LayerProbePagerAdapter layerProbePagerAdapter;
    private static FloatingActionButton fab;
    private static TabLayout tabLayout;
    public static int pageSelected;
    private ViewPager viewPager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pageSelected = 0;
        final String nameObject = getArguments().getString("nameObj");
        final String nameExc = getArguments().getString("nameExc");
        String typeExc = getArguments().getString("typeExc");
        final long keyExc = getArguments().getLong("keyExc");
        double absoluteElevation = getArguments().getDouble("absoluteElevation");
        getActivity().setTitle(typeExc + " " + nameExc + " - " + nameObject);
        View view = inflater.inflate(R.layout.frag_layer_probe, container, false);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        // запускаем ViewPager с TabLayout
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        layerProbePagerAdapter = new LayerProbePagerAdapter(getChildFragmentManager (), getActivity(), nameObject, nameExc, typeExc, absoluteElevation, keyExc);
        viewPager.setAdapter(layerProbePagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    pageSelected = 0;
                    fab.show();
                } else {
                    pageSelected = 1;
                    fab.show();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    fragmentTransaction.remove(prev);
                }
                if (pageSelected == 0) {
                    fragmentTransaction.addToBackStack(null);
                    DialogFragment dialogFragment;
                    if (LayerFragList.list.size() == 0) {
                        dialogFragment = DialogLayer.newInstance(getString(R.string.title_new_layer_dialog), nameObject, nameExc, 0, keyExc);
                    } else {
                        dialogFragment = DialogLayer.newInstance(getString(R.string.title_new_layer_dialog), nameObject, nameExc, LayerFragList.list.get(LayerFragList.list.size() - 1).getEndDepth(), keyExc);
                    }
                    dialogFragment.show(fragmentTransaction, "dialog");
                } else if (pageSelected == 1){
                    fragmentTransaction.addToBackStack(null);
                    DialogFragment dialogFragment = DialogProbe.newInstance(getString(R.string.title_new_probe_dialog), nameObject, keyExc);
                    dialogFragment.show(fragmentTransaction, "dialog");
                }
            }
        });

        return view;
    }

    public static FloatingActionButton getFloatingActionButton() {
        return fab;
    }
}
