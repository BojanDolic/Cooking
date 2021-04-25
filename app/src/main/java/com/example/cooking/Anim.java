package com.example.cooking;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class Anim {

    void InputError(TextInputLayout layout, Toast toast) {

        PropertyValuesHolder layoutX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.1f, 1f, 1.1f, 1f);
        PropertyValuesHolder layoutY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.1f, 1f, 1.1f, 1f);

        ObjectAnimator layoutAnim = ObjectAnimator.ofPropertyValuesHolder(layout, layoutX, layoutY);
        layoutAnim.setDuration(1000);

        layout.setError("");

        if(toast != null)
            toast.show();

        layoutAnim.start();

    }

    void InputError(EditText layout, Toast toast) {

        PropertyValuesHolder layoutX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.1f, 1f, 1.1f, 1f);
        PropertyValuesHolder layoutY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.1f, 1f, 1.1f, 1f);

        ObjectAnimator layoutAnim = ObjectAnimator.ofPropertyValuesHolder(layout, layoutX, layoutY);
        layoutAnim.setDuration(1000);

        layout.setError("");

        if(toast != null)
            toast.show();

        layoutAnim.start();

    }

}
