package com.bjelac.passwordmanager.utils;

import android.content.Context;
import android.widget.Toast;

public class NotifyUser {
    public static void notify (Context context, String  message) {
        Toast.makeText(context,
                message,
                Toast.LENGTH_LONG).show();
    }
}
