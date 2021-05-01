package com.electroniccode.cooking.Adapteri;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.electroniccode.cooking.Fragmenti.ReceptTutorijalFragment;

public class TutorijalAdapter extends FragmentPagerAdapter {


    public TutorijalAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return new ReceptTutorijalFragment(position);
    }


    @Override
    public int getCount() {
        return 10;
    }
}
