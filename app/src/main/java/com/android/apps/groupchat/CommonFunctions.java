package com.android.apps.groupchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

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

class KeyBoard {

    public static void toggle(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isActive()){
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
        } else {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY); // show
        }
    }//end method
}//end class

class AlertDialogs {
    static Activity activity;

    public static void showErrorDialog(Activity _activity, Exception e) {
        activity = _activity;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Error");
        alertDialogBuilder.setMessage(e.getMessage().toUpperCase());
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        alertDialogBuilder.create().show();
    }
}

class ParseDatastore {
    static Activity activity;
    static MessagesAdapter objMessagesAdapter;
    static List<ParseObject> listMessages;
    static ListView listViewMessages;

    public static void ReloadDatastore(Activity _activity, ListView _listViewMessages) {
        activity = _activity;
        listViewMessages = _listViewMessages;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupMessages");
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> MessageParseObjects, ParseException e) {
                if (e == null) {
                    listMessages = MessageParseObjects;
                    objMessagesAdapter = new MessagesAdapter(listViewMessages.getContext(), listMessages);
                    listViewMessages.setAdapter(objMessagesAdapter);
                    objMessagesAdapter.notifyDataSetChanged();
                } else {
                    AlertDialogs.showErrorDialog(activity, e);
                }
            }
        });
    }
}
