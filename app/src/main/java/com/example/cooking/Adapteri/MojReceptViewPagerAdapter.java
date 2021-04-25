package com.example.cooking.Adapteri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.bumptech.glide.RequestManager;
import com.example.cooking.Fragmenti.MojiReceptiFragment;
import com.example.cooking.Fragmenti.PrijavljeniReceptFragment;
import com.google.firebase.firestore.FirebaseFirestore;

public class MojReceptViewPagerAdapter extends FragmentPagerAdapter {

    RequestManager glide;
    String userID;
    FirebaseFirestore db;


    public MojReceptViewPagerAdapter(@NonNull FragmentManager fm, int behavior, FirebaseFirestore db, String userID, RequestManager glide) {
        super(fm, behavior);
        this.db = db;
        this.userID = userID;
        this.glide = glide;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new MojiReceptiFragment(db, glide, userID);

            case 1: return new PrijavljeniReceptFragment(glide, db, userID);

        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        String title = "";

        switch (position) {

            case 0: title = "Moji recepti"; break;
            case 1: title = "Prijavljeni recepti"; break;
        }
        return title;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
