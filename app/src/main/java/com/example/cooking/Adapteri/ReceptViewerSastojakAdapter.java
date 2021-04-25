package com.example.cooking.Adapteri;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cooking.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ReceptViewerSastojakAdapter extends RecyclerView.Adapter<ReceptViewerSastojakAdapter.SastojakHolder> {


    List<String> sastojci = new ArrayList<>();

    public ReceptViewerSastojakAdapter(List<String> sastojci) {
        this.sastojci = sastojci;
    }

    @NonNull
    @Override
    public SastojakHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sastojak_preview, parent, false);

        return new SastojakHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull SastojakHolder holder, int position) {

        holder.redniBrojSastojka.setText(String.format(Locale.GERMAN,"%d.", position +1));
        holder.imeSastojka.setText(sastojci.get(position));

    }

    @Override
    public int getItemCount() {
        return sastojci.size();
    }


    class SastojakHolder extends RecyclerView.ViewHolder {

        TextView redniBrojSastojka, imeSastojka;


        public SastojakHolder(@NonNull View itemView) {
            super(itemView);

            redniBrojSastojka = itemView.findViewById(R.id.sastojak_preview_redniBroj);
            imeSastojka = itemView.findViewById(R.id.sastojak_preview_imeSastojka);
        }
    }

}
