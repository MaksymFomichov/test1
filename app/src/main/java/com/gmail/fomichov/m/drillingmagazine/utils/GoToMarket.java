package com.gmail.fomichov.m.drillingmagazine.utils;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import com.gmail.fomichov.m.drillingmagazine.MainActivity;

public class GoToMarket {

    private GoToMarket() {
    }

    public static void getStar(MainActivity activity) {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            activity.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        }
    }
}
