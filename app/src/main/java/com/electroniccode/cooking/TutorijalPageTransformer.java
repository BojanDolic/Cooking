package com.electroniccode.cooking;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class TutorijalPageTransformer implements ViewPager.PageTransformer {

    private static final float MIN = 0.75f;

    @Override
    public void transformPage(@NonNull View page, float position) {

        page.setTranslationX(-position * page.getWidth());
        page.setCameraDistance(12000);

        if (position < 0.5 && position > -0.5) {
            page.setVisibility(View.VISIBLE);
        } else {
            page.setVisibility(View.INVISIBLE);
        }


        if (position < -1){ // [-Besk, -1]
            page.setAlpha(0);

        }
        else if (position <= 0) {    // [-1,0]
            page.setAlpha(1);
            page.setRotationY(180 *(1-Math.abs(position)+1));
            page.setScaleX(1f - -position);
            page.setScaleY(1f - -position);

        }
        else if (position <= 1) {    // (0,1]
            page.setAlpha(1);
            page.setRotationY(-180 *(1-Math.abs(position)+1));
            page.setScaleX(Math.abs(position - 1f));
            page.setScaleY(Math.abs(position - 1f));

        }
        else {    // (1,+Besk]
            page.setAlpha(0);

        }


    }

}
