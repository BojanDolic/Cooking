package com.electroniccode.cooking.Adapteri;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.electroniccode.cooking.R;
import com.electroniccode.cooking.ZdravaHranaPojo;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class ZdravaHranaAdapter extends FirestoreRecyclerAdapter<ZdravaHranaPojo, ZdravaHranaAdapter.ZdravaHranaHolder> {


    public interface OnZdravaHranaCardClick {
        void onZdravaHranaClicked(DocumentSnapshot snapshot);
    }

    private OnZdravaHranaCardClick clickListener;

    private final RequestManager glide;


    public ZdravaHranaAdapter(@NonNull FirestoreRecyclerOptions<ZdravaHranaPojo> options, RequestManager glide) {
        super(options);
        this.glide = glide;
    }

    @Override
    protected void onBindViewHolder(@NonNull ZdravaHranaHolder holder, int position, @NonNull ZdravaHranaPojo model) {

        holder.tekst.setText(model.getIme());

        glide.load(model.getSlika())
                .into(holder.slika);



    }

    @NonNull
    @Override
    public ZdravaHranaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.zdrava_hrana_list_card_element, parent, false);
        return new ZdravaHranaHolder(v);
    }

    public class ZdravaHranaHolder extends RecyclerView.ViewHolder {

        TextView tekst;
        ImageView slika;


        public ZdravaHranaHolder(@NonNull View itemView) {
            super(itemView);

            tekst = itemView.findViewById(R.id.zdrava_hrana_item_naslov);
            slika = itemView.findViewById(R.id.zdrava_hrana_item_slika);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && clickListener != null) {

                        clickListener.onZdravaHranaClicked(getSnapshots().getSnapshot(position));

                    }
                }
            });


        }
    }

    public void setOnZdravaHranaCardClick(OnZdravaHranaCardClick clickListener) {
        this.clickListener = clickListener;
    }

}
