package com.electroniccode.cooking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electroniccode.cooking.Adapteri.ReceptViewerSastojakAdapter;

import java.util.ArrayList;
import java.util.List;

public class SastojakPreviewFragment extends Fragment {


    private List<String> sastojci = new ArrayList<>();

    public SastojakPreviewFragment(List<String> sastojci) {
        this.sastojci = sastojci;
    }

    private RecyclerView sastojciPreviewRecyclerView;

    public SastojakPreviewFragment() { }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.sastojci_fragment, container, false);

        sastojciPreviewRecyclerView = v.findViewById(R.id.sastojci_preview_recycler);
        sastojciPreviewRecyclerView.setAdapter(new ReceptViewerSastojakAdapter(sastojci));
        sastojciPreviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return v;
    }

}
