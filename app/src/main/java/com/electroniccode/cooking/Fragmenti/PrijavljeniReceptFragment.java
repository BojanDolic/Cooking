package com.electroniccode.cooking.Fragmenti;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.electroniccode.cooking.Adapteri.PrijavljeniReceptiAdapter;
import com.electroniccode.cooking.R;
import com.electroniccode.cooking.Recept;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PrijavljeniReceptFragment extends Fragment {

    private FirebaseFirestore db;
    private RequestManager glide;
    private String userID;

    private PrijavljeniReceptiAdapter adapter;

    public PrijavljeniReceptFragment(RequestManager glide, FirebaseFirestore db, String userID) {
        this.db = db;
        this.glide = glide;
        this.userID = userID;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.moji_recepti_fragment, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.moji_recepti_recycler);

        Query query = db.collection("objaveKorisnika")
                .whereEqualTo("imeAutora", userID)
                .whereGreaterThanOrEqualTo("brojPrijava", 1);

        FirestoreRecyclerOptions<Recept> opcije = new FirestoreRecyclerOptions.Builder<Recept>()
                .setLifecycleOwner(this)
                .setQuery(query, Recept.class)
                .build();

        adapter = new PrijavljeniReceptiAdapter(opcije, glide, db, getContext(), recyclerView);


        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
