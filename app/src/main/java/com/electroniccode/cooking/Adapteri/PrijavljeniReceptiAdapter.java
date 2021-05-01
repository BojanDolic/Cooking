package com.electroniccode.cooking.Adapteri;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.electroniccode.cooking.R;
import com.electroniccode.cooking.Recept;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PrijavljeniReceptiAdapter extends FirestoreRecyclerAdapter<Recept, PrijavljeniReceptiAdapter.ViewHolder> {



    private RequestManager glide;
    private FirebaseFirestore db;
    private Context context;
    private RecyclerView recyclerViewRef;

    public PrijavljeniReceptiAdapter(@NonNull FirestoreRecyclerOptions<Recept> options, RequestManager glide, FirebaseFirestore db, Context context,@NonNull RecyclerView recyclerViewRef) {
        super(options);
        this.glide = glide;
        this.db = db;
        this.context = context;
        this.recyclerViewRef = recyclerViewRef;
    }




    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final Recept model) {

        holder.nazivRecepta.setText(model.getNaslovRecepta().get(0));

        glide.load(model.getSlikaRecepta())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(holder.slikaRecepta);

        //document(getSnapshots().getSnapshot(holder.getAdapterPosition()).getId()).collection("prijave")

        holder.prijaveProgressBar.setVisibility(View.VISIBLE);

        db.collection("objaveKorisnika/" + getSnapshots().getSnapshot(holder.getAdapterPosition()).getId() + "/prijave").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()) {

                    List<DocumentSnapshot> snapshotList = new ArrayList<>();

                    try {
                        snapshotList = task.getResult().getDocuments();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    holder.prijaveContent.removeAllViews();

                    for(DocumentSnapshot snapshot : snapshotList) {

                        if(snapshot.exists()) {


                            String tekstPrijave = snapshot.getString("tekstPrijave");

                            View prijavaCard = LayoutInflater.from(holder.slikaRecepta.getContext()).inflate(R.layout.prijava_card_design, (ViewGroup) holder.prijaveContent.findViewById(android.R.id.content) , false);

                            TextView tekst = prijavaCard.findViewById(R.id.tekst_prijave_card);

                            tekst.setText(tekstPrijave);

                            holder.prijaveContent.addView(prijavaCard);

                            Log.d("PrijavljeniRecepti", "onComplete: " + tekstPrijave);
                        }

                    }

                    holder.prijaveProgressBar.setVisibility(View.GONE);

                }

            }
        });

    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.prijavljen_recept_card, parent, false);
        return new ViewHolder(v);
    }




    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView slikaRecepta;
        TextView  nazivRecepta;

        Button otvoriPrijave;
        LinearLayout prijaveContent;
        CardView prijaveCard;

        LinearLayout root;

        ProgressBar prijaveProgressBar;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            slikaRecepta = itemView.findViewById(R.id.slika_recepta_prijavljeni);

            nazivRecepta = itemView.findViewById(R.id.naziv_recepta_prijavljeni);
            prijaveContent = itemView.findViewById(R.id.prijava_card_content);
            prijaveProgressBar = itemView.findViewById(R.id.prijave_progress_bar);
            prijaveCard = itemView.findViewById(R.id.recept_card_cardview);

            root = itemView.findViewById(R.id.prijava_card_container);


            otvoriPrijave = itemView.findViewById(R.id.prijavljen_recept_otvori_btn);


            otvoriPrijave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(prijaveContent.getVisibility() == View.VISIBLE) {

                        prijaveContent.setVisibility(View.GONE);
                        otvoriPrijave.setRotationX(0);


                    } else if(prijaveContent.getVisibility() == View.GONE){

                        prijaveContent.setVisibility(View.VISIBLE);
                        otvoriPrijave.setRotationX(180);
                    }

                }
            });

        }


    }

}
