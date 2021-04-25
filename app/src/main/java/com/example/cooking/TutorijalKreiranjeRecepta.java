package com.example.cooking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;

import com.example.cooking.Adapteri.TutorijalAdapter;
import com.google.android.material.tabs.TabLayout;

public class TutorijalKreiranjeRecepta extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    Button nextBtn, backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorijal_kreiranje_recepta);


        viewPager = findViewById(R.id.tutorijal_viewpager);
        tabLayout = findViewById(R.id.tutorijal_tablayout);
        nextBtn = findViewById(R.id.tutorijal_dalje_btn);
        backBtn = findViewById(R.id.tutorijal_nazad_btn);

        backBtn.setVisibility(View.GONE);

        viewPager.setAdapter(new TutorijalAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));

        viewPager.setPageTransformer(false, new TutorijalPageTransformer());


        tabLayout.setupWithViewPager(viewPager, true);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {

                if(position > 0)
                    backBtn.setVisibility(View.VISIBLE);
                else
                    backBtn.setVisibility(View.GONE);


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(viewPager.getCurrentItem() >= 9)
                    finish();

                viewPager.setCurrentItem(viewPager.getCurrentItem() +1, true);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() -1, true);

            }
        });


    }
}
