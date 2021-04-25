package com.example.cooking.Adapteri;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.cooking.Komentar;
import com.example.cooking.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class KomentarAdapter extends FirestoreRecyclerAdapter<Komentar, KomentarAdapter.KomentarHolder> {


    public interface OnKomentarClicked {
        void onKomentarLongClick(DocumentSnapshot snapshot);
    }

    OnKomentarClicked onKomentarClicked;

    FirebaseFirestore db;
    private final RequestManager glide;


    public KomentarAdapter(@NonNull FirestoreRecyclerOptions<Komentar> options, FirebaseFirestore db, RequestManager glide) {
        super(options);
        this.db = db;
        this.glide = glide;
    }

    @Override
    protected void onBindViewHolder(@NonNull final KomentarHolder holder, int position, @NonNull Komentar model) {

        holder.komentarText.setText(model.getKomentar());

        db.collection("korisnici").document(model.getAutor()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    holder.imeAutora.setText(documentSnapshot.getString("imeKorisnika"));

                    glide.load(documentSnapshot.getString("profilnaSlika"))
                            .placeholder(R.drawable.profile_icon)
                            .apply(RequestOptions.circleCropTransform())
                            .into(holder.slikaAutora);
                }
                else {
                    holder.imeAutora.setText("N/A");

                    glide.load(R.drawable.profile_icon)
                            .apply(RequestOptions.circleCropTransform())
                            .into(holder.slikaAutora);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                holder.imeAutora.setText("N/A");
            }
        });


    }

    @NonNull
    @Override
    public KomentarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.komentar_layout, parent, false);

        return new KomentarHolder(v);
    }



    class KomentarHolder extends RecyclerView.ViewHolder {


        TextView komentarText, imeAutora;
        ImageView slikaAutora;

        public KomentarHolder(@NonNull View itemView) {
            super(itemView);

            komentarText = itemView.findViewById(R.id.komentar_text);
            imeAutora = itemView.findViewById(R.id.komentar_layout_ime_autora_text);
            slikaAutora = itemView.findViewById(R.id.komentar_layout_slika_autora);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onKomentarClicked.onKomentarLongClick(getSnapshots().getSnapshot(getAdapterPosition()));
                    return true;
                }
            });


        }

    }

    public void setOnKomentarClickListener(OnKomentarClicked onKomentarClicked) {
        this.onKomentarClicked = onKomentarClicked;
    }

}
