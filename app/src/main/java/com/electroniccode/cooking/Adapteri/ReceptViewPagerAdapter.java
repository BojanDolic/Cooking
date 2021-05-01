package com.electroniccode.cooking.Adapteri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.electroniccode.cooking.SastojakPreviewFragment;

import java.util.List;

public class ReceptViewPagerAdapter extends FragmentPagerAdapter {

    private List<String> sastojci;
    private List<String> koraci;

    public ReceptViewPagerAdapter(@NonNull FragmentManager fm, int behavior, List<String> sastojci, List<String> koraci) {
        super(fm, behavior);
        this.sastojci = sastojci;
        this.koraci = koraci;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                return new SastojakPreviewFragment(sastojci);


            case 1:
                return new SastojakPreviewFragment(koraci);
        }


        return null;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        String title = "Greška";

        if(position == 0)
            title = "Sastojci";
        else if(position == 1)
            title = "Koraci";


        return title;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
