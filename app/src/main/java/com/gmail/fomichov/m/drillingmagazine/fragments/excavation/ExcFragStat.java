package com.gmail.fomichov.m.drillingmagazine.fragments.excavation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.fomichov.m.drillingmagazine.R;

public class ExcFragStat extends Fragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_stat_exc, container, false);

        return view;
    }

    public static ExcFragStat newInstance() {
        ExcFragStat fragment = new ExcFragStat();
        return fragment;
    }
}
