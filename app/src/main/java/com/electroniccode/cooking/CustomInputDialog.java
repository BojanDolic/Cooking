package com.electroniccode.cooking;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class CustomInputDialog {


    public CustomInputDialog() {

    }


    public androidx.appcompat.app.AlertDialog.Builder createDialog(@NonNull Activity context, String title, String message ) {


        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.posalji_text, null);
        builder.setNegativeButton(R.string.otkazi_text, null);

        return builder;
    }

    public static androidx.appcompat.app.AlertDialog.Builder createYesNoDialog(@NonNull Context context, String title, String message) {

        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Prijavi", null);
        builder.setNegativeButton("Odustani", null);

        return builder;
    }

}
