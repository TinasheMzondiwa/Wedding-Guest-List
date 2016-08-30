package com.tinashe.guestlist.utils;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;


/**
 * Created by tinashe on 2015/03/05.
 */
public class SnackBarUtil {

    public static void show(View view, String text, int duration) {

        make(view, text, duration)
                .show();
    }

    public static void show(View view, int text, int duration) {

        make(view, text, duration)
                .show();
    }

    public static Snackbar make(View view, String text, int duration) {
        Snackbar snackbar = Snackbar.make(view, text, duration);
        theme(snackbar);
        return snackbar;

    }

    public static Snackbar make(View view, int textResId, int duration) {
        Snackbar snackbar = Snackbar.make(view, textResId, duration);
        theme(snackbar);
        return snackbar;
    }

    /**
     * theming the snackbar
     *
     * @param snackbar
     */
    private static void theme(Snackbar snackbar) {
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(Color.DKGRAY);
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.setActionTextColor(Color.YELLOW);
    }

}
