package com.electroniccode.cooking;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddCommentDialog extends DialogFragment {

    public interface NoviKomentarListener {
        void getKomentarText(String tekst);
    }


    private NoviKomentarListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.new_comment_dialog_layout, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        final LinearLayout mainLayout = view.findViewById(R.id.comment_dialog_layout);
        Button dodajKomentarBtn = view.findViewById(R.id.comment_dialog_dodaj_komentar);

        final TextInputLayout inputLayout = view.findViewById(R.id.new_comment_textinput_layout);
        final TextInputEditText tekstKomentaraEditText = view.findViewById(R.id.komentar_edittext);
        final ProgressBar loadingBar = view.findViewById(R.id.comment_progress_bar);


        loadingBar.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);


        dodajKomentarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String komentarTekst = tekstKomentaraEditText.getText().toString();


                if(!TextUtils.isEmpty(komentarTekst)) {
                    if(komentarTekst.length() <= 150 && komentarTekst.length() >= 5) {
                        loadingBar.setVisibility(View.VISIBLE);
                        mainLayout.setVisibility(View.GONE);
                        listener.getKomentarText(komentarTekst);
                    }
                    else
                    {
                        inputLayout.setError("Komentar ne može biti duži od 150 karaktera i kraći od 5 karaktera !");
                    }
                } else
                {
                    inputLayout.setError("Tekst komentara ne smije biti prazan !");
                }

            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (NoviKomentarListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "implement NoviComment");
        }


    }

    @Override
    public void onResume() {
        super.onResume();

        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height =  WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);

    }
}
