package com.electroniccode.cooking.Adapteri;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Locale;

public class MojReceptAdapter extends FirestoreRecyclerAdapter<Recept, MojReceptAdapter.MojReceptHolder> {

    public interface OnMojReceptClick {
        void OnMojReceptClicked(DocumentSnapshot snapshot);
        void OnMojReceptLongClicked(DocumentSnapshot snapshot, DocumentReference reference, View holder);
    }

    private OnMojReceptClick onMojReceptClick;


    private RequestManager glide;

    public MojReceptAdapter(@NonNull FirestoreRecyclerOptions<Recept> options, RequestManager glide) {
        super(options);
        this.glide = glide;
    }

    @Override
    protected void onBindViewHolder(@NonNull MojReceptHolder holder, int position, @NonNull Recept model) {

        holder.nazivRecepta.setText(model.getNaslovRecepta().get(0));
        holder.tezinaPripremeRecepta.setText(model.getTezinu(model.getTezinaPripreme()));
        holder.brojOsoba.setText(String.format(Locale.getDefault(), "%d", model.getBrojOsoba()));
        holder.vrijemePripreme.setText(String.format(Locale.getDefault(), "%d m", model.getVrijemePripreme()));
        holder.ocjena.setText(String.format(Locale.getDefault(), "%d", model.getBrojSvidjanja()));

        Log.d("TEST", "onBindViewHolder: " + model.getNaslovRecepta().get(0));

        holder.autorInfo.setVisibility(View.GONE);

        holder.kategorijaJela.setText(model.getKategorijaJela());

        glide.load(model.getSlikaRecepta())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(holder.slikaRecepta);




    }

    @NonNull
    @Override
    public MojReceptHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recept_card, parent, false);
        return new MojReceptHolder(v);
    }

    public class MojReceptHolder extends RecyclerView.ViewHolder {


        TextView nazivRecepta, tezinaPripremeRecepta, brojOsoba, vrijemePripreme, ocjena, kategorijaJela;
        ImageView slikaRecepta;
        CardView receptCard;
        RelativeLayout autorInfo;

        public MojReceptHolder(@NonNull final View itemView) {
            super(itemView);

            nazivRecepta = itemView.findViewById(R.id.naziv_recepta_text);
            tezinaPripremeRecepta = itemView.findViewById(R.id.tezina_pripreme_text);
            brojOsoba = itemView.findViewById(R.id.broj_osoba_text);
            vrijemePripreme = itemView.findViewById(R.id.vrijeme_pripreme_text);
            ocjena = itemView.findViewById(R.id.ocjena_recepta_text);
            kategorijaJela = itemView.findViewById(R.id.kategorija_jela_card_text);
            autorInfo = itemView.findViewById(R.id.recept_card_autor_relative_layout);
            receptCard = itemView.findViewById(R.id.recept_card_cardview);


            slikaRecepta = itemView.findViewById(R.id.slika_recepta);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMojReceptClick.OnMojReceptClicked(getSnapshots().getSnapshot(getAdapterPosition()));
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onMojReceptClick.OnMojReceptLongClicked(getSnapshots().getSnapshot(getAdapterPosition()), getSnapshots().getSnapshot(getAdapterPosition()).getReference(),itemView);
                    return true;
                }
            });

            Animation anim = AnimationUtils.loadAnimation(itemView.getContext(),android.R.anim.slide_in_left);
            itemView.setAnimation(anim);

        }
    }

    public void setOnMojReceptClicked(OnMojReceptClick onMojReceptClick) {
        this.onMojReceptClick = onMojReceptClick;
    }

}
