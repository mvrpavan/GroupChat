package com.android.apps.groupchat;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by pavan on 11/2/2015.
 */
public class CommonFunctions {

    public static void disableEnableControls(boolean enable, ViewGroup vg){
        for (int i = 0; i < vg.getChildCount(); i++){
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup){
                disableEnableControls(enable, (ViewGroup)child);
            }
        }
    }
}
