package com.example.cooking.Fragmenti;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cooking.R;


public class FilterFragment extends DialogFragment {

    public interface OnReceptFilter {
        void getFilterResults(String kategorija, int tezinaPripreme, int vrijemePripreme);
    }

    private OnReceptFilter filter;


    private String[] kategorijeRecepta = {"Sve", "Predjelo", "Gotovo jelo", "Supa/Čorba", "Riba", "Roštilj", "Salata", "Varivo", "Kompot", "Torta", "Kolač"};
    private String[] tezinePripreme = { "Sve težine", "Lako", "Srednje", "Teško"};
    private String[] vrijemePripremeTexts = { "Sve", "Do 15 minuta", "Do 30 minuta", "Do 45 minuta", "Do 60 minuta", "Do 90 minuta", "Do 120 minuta"};

    String kategorija = "";
    private int tezinaPripreme = 0;
    private int vrijemePripreme = 0;

    private Button filtriraj,odustani;
    private Spinner kategorijaReceptaSpinner, tezinaPripremeSpinner, vrijemePripremeSpinner;


    public FilterFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.filtriraj_popup, container, false);

        kategorijaReceptaSpinner = view.findViewById(R.id.filter_kategorija_spinner);
        tezinaPripremeSpinner = view.findViewById(R.id.filter_tezinaPripreme_spinner);
        vrijemePripremeSpinner = view.findViewById(R.id.filter_vrijemePripreme_spinner);
        filtriraj = view.findViewById(R.id.filtriraj_da_btn);
        odustani = view.findViewById(R.id.filtriraj_ne_btn);


        setUpKategorijaSpinner(kategorijaReceptaSpinner, inflater.getContext());
        setUpTezinaSpinner(tezinaPripremeSpinner, inflater.getContext());
        setUpVrijemePripremeSpinner(vrijemePripremeSpinner, inflater.getContext());


        filtriraj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
                filter.getFilterResults(kategorija, tezinaPripreme, vrijemePripreme);



            }
        });

        odustani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        return view;
    }

    private void setUpVrijemePripremeSpinner(Spinner spinner, Context context) {
        spinner.setAdapter(new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, vrijemePripremeTexts));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        vrijemePripreme = 0; break;
                    case 1:
                        vrijemePripreme = 15; break;
                    case 2:
                        vrijemePripreme = 30; break;
                    case 3:
                        vrijemePripreme = 45; break;
                    case 4:
                        vrijemePripreme = 60; break;
                    case 5:
                        vrijemePripreme = 90; break;
                    case 6:
                        vrijemePripreme = 120; break;
                    case 7:
                        vrijemePripreme = 130; break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setUpKategorijaSpinner(Spinner spinner, Context c) {

        spinner.setAdapter(new ArrayAdapter<String>(c, R.layout.support_simple_spinner_dropdown_item, kategorijeRecepta));


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                kategorija = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setUpTezinaSpinner(Spinner spinner, Context c) {
        spinner.setAdapter(new ArrayAdapter<String>(c, R.layout.support_simple_spinner_dropdown_item, tezinePripreme));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tezinaPripreme = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            filter = (OnReceptFilter) context;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
