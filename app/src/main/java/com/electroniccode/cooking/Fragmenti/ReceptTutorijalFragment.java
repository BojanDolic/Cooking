package com.electroniccode.cooking.Fragmenti;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.electroniccode.cooking.R;

public class ReceptTutorijalFragment extends Fragment {



    private int page;

    public ReceptTutorijalFragment(int page) {
        this.page = page;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        int layout = 0;
        switch (page) {
            case 0:
                layout = R.layout.layout_tutorijal_1; break;

            case 1:
                layout = R.layout.layout_tutorijal_2; break;

            case 2:
                layout = R.layout.layout_tutorijal_3; break;

            case 3:
                layout = R.layout.layout_tutorijal4; break;

            case 4:
                layout = R.layout.layout_tutorijal_5; break;

            case 5:
                layout = R.layout.layout_tutorijal_6; break;

            case 6:
                layout = R.layout.layout_tutorijal_7; break;

            case 7:
                layout = R.layout.layout_tutorijal_8; break;

            case 8:
                layout = R.layout.layout_tutorijal_9; break;

            case 9:
                layout = R.layout.layout_tutorijal_10; break;
        }

        View v = getActivity().getLayoutInflater().inflate(layout, container, false);

        v.setTag(page);

        return v;

    }
}
