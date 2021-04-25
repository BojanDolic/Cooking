package com.example.cooking;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PodesavanjeProfilaBottomSheet extends BottomSheetDialogFragment {

    public static String PROMJENA_IMENA_CODE = "changeNameSettings";
    public static String PROMJENA_SLIKE_CODE = "changeImageSettings";

    private SettingsBottomSheetListener listener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.podesavanja_bottom_sheet, container, false);


        RelativeLayout promjenaImenaBtn = view.findViewById(R.id.settings_change_name_btn);
        RelativeLayout promjenaSlikeBtn = view.findViewById(R.id.settings_change_profile_picture_btn);

        promjenaImenaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnOptionClicked(PROMJENA_IMENA_CODE);
                dismiss();
            }
        });

        promjenaSlikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnOptionClicked(PROMJENA_SLIKE_CODE);
                dismiss();
            }
        });


        return view;
    }

    public interface SettingsBottomSheetListener {
        void OnOptionClicked(String opcija);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (SettingsBottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "implementacija BottomSheet-a");
        }

    }


}
