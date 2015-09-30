package com.mikhaildev.profiru.util;

import android.app.Activity;
import android.widget.Toast;

import com.mikhaildev.profiru.R;
import com.mikhaildev.profiru.exception.ApiException;
import com.mikhaildev.profiru.exception.NetworkConnectionException;

/**
 * Created by E.Mikhail on 30.09.2015.
 */
public class UiUtils {

    private UiUtils() {

    }

    public static void showException(Activity activity, Exception e) {
        if (e.getClass().equals(NetworkConnectionException.class)) {
            Toast.makeText(activity, R.string.internet_connection_error, Toast.LENGTH_LONG).show();
        } else if (e.getClass().equals(ApiException.class)) {
            ApiException apiException = (ApiException) e;
            Toast.makeText(activity, apiException.getMessageResourceId(), Toast.LENGTH_LONG).show();
        }
    }
}
