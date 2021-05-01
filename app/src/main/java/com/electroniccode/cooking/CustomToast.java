package com.electroniccode.cooking;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class CustomToast extends Toast {


    public CustomToast(Context context) {
        super(context);
    }

    public static Toast info(@NonNull Context c, CharSequence text, int trajanje) {

        View layout = View.inflate(c,R.layout.custom_toast, null);

        TextView textView = layout.findViewById(R.id.custom_toast_text);
        textView.setText(text);

        ImageView img = layout.findViewById(R.id.custom_toast_img);
        img.setImageResource(R.drawable.info_icon);
        img.setImageTintList(ColorStateList.valueOf(Color.WHITE));

        Toast toast = new Toast(c);
        toast.setView(layout);
        toast.setDuration(trajanje);

        return toast;
    }

    public static Toast uspjesno(@NonNull Context c, CharSequence text, int trajanje) {

        View layout = View.inflate(c,R.layout.custom_toast, null);

        LinearLayout layout1 = layout.findViewById(R.id.custom_toast_root);
        layout1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5dbf17")));

        TextView textView = layout.findViewById(R.id.custom_toast_text);
        textView.setText(text);

        ImageView img = layout.findViewById(R.id.custom_toast_img);
        img.setImageResource(R.drawable.info_icon);
        img.setImageTintList(ColorStateList.valueOf(Color.WHITE));

        Toast toast = new Toast(c);
        toast.setView(layout);
        toast.setDuration(trajanje);

        return toast;
    }

    public static Toast error(@NonNull Context c, CharSequence text, int trajanje) {
        View layout = View.inflate(c,R.layout.custom_toast, null);

        TextView textView = layout.findViewById(R.id.custom_toast_text);
        LinearLayout layout1 = layout.findViewById(R.id.custom_toast_root);
        ImageView img = layout.findViewById(R.id.custom_toast_img);
        layout1.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        img.setImageResource(R.drawable.error_icon);
        img.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        textView.setText(text);

        Toast toast = new Toast(c);
        toast.setView(layout);
        toast.setDuration(trajanje);

        return toast;
    }

}
