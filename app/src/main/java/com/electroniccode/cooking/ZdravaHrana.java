package com.electroniccode.cooking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.electroniccode.cooking.Adapteri.ZdravaHranaAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ZdravaHrana extends AppCompatActivity implements ZdravaHranaAdapter.OnZdravaHranaCardClick {


    RecyclerView zdravaHranaRecycler;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.HasStatusBarStyle);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zdrava_hrana);

        zdravaHranaRecycler = findViewById(R.id.zdrava_hrana_recycler);

        db = FirebaseFirestore.getInstance();

        Query query = db.collection("zdravaHrana");


        FirestoreRecyclerOptions<ZdravaHranaPojo> recyclerOptions = new FirestoreRecyclerOptions.Builder<ZdravaHranaPojo>()
                .setLifecycleOwner(this)
                .setQuery(query, ZdravaHranaPojo.class)
                .build();

        ZdravaHranaAdapter adapter = new ZdravaHranaAdapter(recyclerOptions, Glide.with(this));


        adapter.setOnZdravaHranaCardClick(this);

        zdravaHranaRecycler.setAdapter(adapter);
        zdravaHranaRecycler.setLayoutManager(new LinearLayoutManager(this));


    }



    @Override
    public void onZdravaHranaClicked(DocumentSnapshot snapshot) {
        Intent i = new Intent(ZdravaHrana.this, ZdravaHranaViewer.class);
        i.putExtra("document_path", snapshot.getReference().getPath());
        startActivity(i);
    }
}
