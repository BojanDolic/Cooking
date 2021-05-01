package com.electroniccode.cooking;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.core.widget.NestedScrollView;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ZdravaHranaViewer extends AppCompatActivity {

    ImageView zdravaHranaImg;
    NestedScrollView nestedScrollView;
    LinearLayout linearLayout;
    CollapsingToolbarLayout collapsingToolbar;
    ContentLoadingProgressBar progressBar;

    static String Doc_PATH = "";

    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.HasStatusBarStyle);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zdrava_hrana_viewer);


        db = FirebaseFirestore.getInstance();


        Toolbar toolbar = (Toolbar) findViewById(R.id.zdrava_hrana_viewer_toolbar);
        setSupportActionBar(toolbar);

        zdravaHranaImg = findViewById(R.id.zdrava_hrana_viewer_image);
        collapsingToolbar = findViewById(R.id.zdrava_hrana_viewer_collapsingtoolbar_layout);
        collapsingToolbar.setTitle("");
        nestedScrollView = findViewById(R.id.zdrava_hrana_viewer_nestedScroll);
        linearLayout = findViewById(R.id.zdrava_hrana_viewer_linearLayout);
        progressBar = findViewById(R.id.zdrava_hrana_viewer_progressBar);



        Intent i = getIntent();

        Bundle bundle = i.getExtras();

        try {
            Doc_PATH = bundle.getString("document_path");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }




        if(Doc_PATH != null) {
            progressBar.setVisibility(View.VISIBLE);
            db.document(Doc_PATH).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {

                    if(snapshot.exists()) {



                        ZdravaHranaPojo klasa = new ZdravaHranaPojo();

                        Object mape = snapshot.get("zaglavlja");

                        klasa = snapshot.toObject(ZdravaHranaPojo.class);

                        collapsingToolbar.setTitle(snapshot.getString("ime"));

                        Glide.with(getApplicationContext())
                                .load(snapshot.getString("slika"))
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .dontTransform()
                                .placeholder(R.drawable.image_placeholder)
                                .into(zdravaHranaImg);

                        //Map<String, String> map = new HashMap<>();

                        //map = klasa.getZaglavlje();

                        List<String> naslovi = new ArrayList<>();
                        naslovi = klasa.getNaslovi();

                        List<String> tekstovi = new ArrayList<>();
                        tekstovi = klasa.getTekstovi();

                        for(int i = 0; i < naslovi.size(); i++) {

                            View v = getLayoutInflater().inflate(R.layout.zdrava_hrana_card_element, (ViewGroup) findViewById(android.R.id.content), false);

                            TextView naslov = v.findViewById(R.id.zdrava_hrana_naslov_zaglavlja);
                            naslov.setText(naslovi.get(i));

                            if(i % 2 == 0) {

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.gravity = Gravity.END;
                                params.bottomMargin = 26;
                                params.topMargin = 26;

                                naslov.setLayoutParams(params);

                            }

                            TextView tekst = v.findViewById(R.id.zdrava_hrana_text);
                            tekst.setText(tekstovi.get(i));

                            linearLayout.addView(v);

                        }


                    }

                    progressBar.setVisibility(View.GONE);
                }
            });
        }

    }


}
