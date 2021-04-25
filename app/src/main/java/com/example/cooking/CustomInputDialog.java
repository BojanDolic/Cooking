package com.example.cooking;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AlertDialogLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
