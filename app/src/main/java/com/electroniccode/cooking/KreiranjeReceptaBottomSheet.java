package com.electroniccode.cooking;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class KreiranjeReceptaBottomSheet extends BottomSheetDialogFragment {

    private ReceptBottomSheetListener listener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recept_bottom_sheet, container, false);


        Button kameraBtn = view.findViewById(R.id.open_camera_button);
        Button galerijaBtn = view.findViewById(R.id.open_galeriju_button);

        kameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnOptionClicked("otvoriKameru");
                dismiss();
            }
        });

        galerijaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnOptionClicked("otvoriGaleriju");
                dismiss();
            }
        });


        return view;
    }

    public interface ReceptBottomSheetListener {
        void OnOptionClicked(String opcija);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ReceptBottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "implementacija BottomSheet-a");
        }

    }
}
