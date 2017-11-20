package com.gmail.fomichov.m.drillingmagazine.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.gmail.fomichov.m.drillingmagazine.R;
import com.gmail.fomichov.m.drillingmagazine.fragments.layer_probe.LayerFragList;
import com.gmail.fomichov.m.drillingmagazine.fragments.layer_probe.ProbeFragList;

public class LayerProbePagerAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private String nameObject;
    private String nameExc;
    private String typeExc;
    private double absoluteElevation;
    private long keyExc;

    public LayerProbePagerAdapter(FragmentManager fm, Context context, String nameObject, String nameExc, String typeExc, double absoluteElevation, long keyExc) {
        super(fm);
        this.context = context;
        this.nameObject = nameObject;
        this.nameExc = nameExc;
        this.typeExc = typeExc;
        this.absoluteElevation = absoluteElevation;
        this.keyExc = keyExc;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return LayerFragList.newInstance(nameObject, nameExc, absoluteElevation, keyExc);
            case 1:
                return ProbeFragList.newInstance(nameObject, nameExc, absoluteElevation, keyExc);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.title_tab_layer);
            case 1:
                return context.getString(R.string.title_tab_probe);
        }
        return null;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
