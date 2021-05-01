package com.electroniccode.cooking.Fragmenti;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.electroniccode.cooking.Adapteri.MojReceptAdapter;
import com.electroniccode.cooking.CustomToast;
import com.electroniccode.cooking.KreiranjeRecepta;
import com.electroniccode.cooking.R;
import com.electroniccode.cooking.Recept;
import com.electroniccode.cooking.ReceptViewerActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.functions.FirebaseFunctions;

public class MojiReceptiFragment extends Fragment implements MojReceptAdapter.OnMojReceptClick {


    FirebaseFirestore db;
    String userID;
    RequestManager glide;

    public MojiReceptiFragment() {}

    public MojiReceptiFragment(FirebaseFirestore db, RequestManager glide, String userID) {
        this.db = db;
        this.glide = glide;
        this.userID = userID;

    }

    FirebaseFunctions functions;

    MojReceptAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.moji_recepti_fragment, container, false);

        functions = FirebaseFunctions.getInstance();

        RecyclerView recyclerView = v.findViewById(R.id.moji_recepti_recycler);

        Query query = db.collection("objaveKorisnika")
                .whereEqualTo("imeAutora", userID);

        FirestoreRecyclerOptions<Recept> opcije = new FirestoreRecyclerOptions.Builder<Recept>()
                .setLifecycleOwner(this)
                .setQuery(query, Recept.class)
                .build();

        adapter = new MojReceptAdapter(opcije, glide);

        adapter.setOnMojReceptClicked(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        return v;
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

    @Override
    public void OnMojReceptClicked(DocumentSnapshot snapshot) {
        Intent i = new Intent(getActivity(), ReceptViewerActivity.class);
        i.putExtra("Document_PATH", snapshot.getReference().getPath());
        i.putExtra("profilEnter", true);
        startActivity(i);
    }

    @Override
    public void OnMojReceptLongClicked(final DocumentSnapshot snapshot, final DocumentReference reference, final View holder) {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Odaberite opciju !")
                .setMessage("Ako želite izmijeniti recept pritisnite Izmijeni !\nAko želite obrisati recept pritisnite obriši !")
                .setNegativeButton("Obriši", null)
                .setPositiveButton("Izmijeni", null);

        final androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();


        Button neBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        neBtn.setTextColor(getActivity().getColor(R.color.crvena));

        neBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                holder.setBackgroundColor(Color.RED);

                androidx.appcompat.app.AlertDialog.Builder deleteBuilder = new androidx.appcompat.app.AlertDialog.Builder(getContext())
                        .setTitle("Želite izbrisati recept ?")
                        .setMessage("Ako obrišete recept, više ga nećete moći vratiti !")
                        .setNegativeButton("OTKAŽI", null)
                        .setPositiveButton("OBRIŠI", null);

                final androidx.appcompat.app.AlertDialog deleteDialog = deleteBuilder.create();
                deleteDialog.show();

                Button otkaziBtn = deleteDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                Button obrisiBtn = deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                obrisiBtn.setTextColor(getActivity().getColor(R.color.crvena));


                otkaziBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDialog.dismiss();
                        holder.setBackgroundColor(Color.TRANSPARENT);
                    }
                });

                obrisiBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Treba obrisati recept sa cloud functions

                        db.document(reference.getPath()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()) {
                                    CustomToast.uspjesno(getActivity(), "Recept uspješno obrisan", 0).show();
                                }
                                else {
                                    CustomToast.error(getActivity(), " Greška pri brisanju recepta", 0).show();
                                }


                            }
                        });


                        deleteDialog.dismiss();
                        holder.setBackgroundColor(Color.TRANSPARENT);

                    }
                });

            }
        });

        Button daBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        daBtn.setTextColor(getActivity().getColor(R.color.zelenaUspjesno));

        daBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent i = new Intent(getActivity(), KreiranjeRecepta.class);

                Bundle extras = new Bundle();
                extras.putBoolean("KreiranjeReceptaAzuriranje", true);
                extras.putString("KreiranjeReceptaDocumentPath", reference.getPath());
                i.putExtras(extras);
                startActivity(i);

            }
        });


    }
}
