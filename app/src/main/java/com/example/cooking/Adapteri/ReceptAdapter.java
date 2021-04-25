package com.example.cooking.Adapteri;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.cooking.R;
import com.example.cooking.Recept;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class ReceptAdapter extends FirestorePagingAdapter<Recept, ReceptAdapter.ReceptHolder> {


    private static final String TAG = "ReceptAdapter";

    FirebaseFirestore db;

    private OnReceptClick receptClickListener;

    public ReceptAdapter(@NonNull FirestorePagingOptions<Recept> options, FirebaseFirestore db) {
        super(options);
        this.db = db;
    }


    @Override
    protected void onBindViewHolder(@NonNull final ReceptHolder holder, int position, @NonNull Recept model) {


        holder.nazivRecepta.setText(model.getNaslovRecepta().get(0));
        holder.tezinaPripremeRecepta.setText(model.getTezinu(model.getTezinaPripreme()));
        holder.brojOsoba.setText(String.format(Locale.getDefault(), "%d", model.getBrojOsoba()));
        holder.vrijemePripreme.setText(String.format(Locale.getDefault(), "%d m", model.getVrijemePripreme()));
        holder.ocjena.setText(String.format(Locale.getDefault(), "%d", model.getBrojSvidjanja()));


        db.collection("korisnici/").document(model.getImeAutora()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists())
                    holder.imeAutora.setText(documentSnapshot.getString("imeKorisnika"));
                else
                    holder.imeAutora.setText("Nepoznat");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                holder.imeAutora.setText("N/A");
            }
        });

        holder.kategorijaJela.setText(model.getKategorijaJela());

        Glide.with(holder.itemView)
                .load(model.getSlikaRecepta())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(holder.slikaRecepta);


        db.collection("korisnici").document(model.getImeAutora()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                    Glide.with(holder.itemView)
                            .load(documentSnapshot.getString("profilnaSlika"))
                            .placeholder(R.drawable.profile_icon)
                            .apply(RequestOptions.circleCropTransform())
                            .into(holder.slikaAutora);
            }
        });

    }

    @NonNull
    @Override
    public ReceptHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recept_card, parent, false);

        return new ReceptHolder(v);
    }

    class ReceptHolder extends RecyclerView.ViewHolder {

        TextView nazivRecepta, tezinaPripremeRecepta, brojOsoba, vrijemePripreme, ocjena, imeAutora, kategorijaJela;
        ImageView slikaRecepta, slikaAutora;


        public ReceptHolder(@NonNull final View itemView) {
            super(itemView);

            nazivRecepta = itemView.findViewById(R.id.naziv_recepta_text);
            tezinaPripremeRecepta = itemView.findViewById(R.id.tezina_pripreme_text);
            brojOsoba = itemView.findViewById(R.id.broj_osoba_text);
            vrijemePripreme = itemView.findViewById(R.id.vrijeme_pripreme_text);
            ocjena = itemView.findViewById(R.id.ocjena_recepta_text);
            imeAutora = itemView.findViewById(R.id.ime_autora_cardtext);
            kategorijaJela = itemView.findViewById(R.id.kategorija_jela_card_text);
            slikaAutora = itemView.findViewById(R.id.slika_autora);

            slikaRecepta = itemView.findViewById(R.id.slika_recepta);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && receptClickListener != null) {

                        receptClickListener.onReceptClicked(getItem(position), position, slikaRecepta);

                    }
                }
            });

        }
    }

    public interface OnReceptClick {
        void onReceptClicked(DocumentSnapshot document, int position, ImageView slika);

    }

    public void setOnReceptClick(OnReceptClick receptClickListener) {
        this.receptClickListener = receptClickListener;
    }

}
