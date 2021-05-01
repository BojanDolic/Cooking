package com.electroniccode.cooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BugReportActivity extends AppCompatActivity {

    TextInputEditText bugInputEditText;
    FloatingActionButton prijaviBugBtn;

    FirebaseFirestore db;
    FirebaseUser user;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_report);

        auth = FirebaseAuth.getInstance();

        bugInputEditText = findViewById(R.id.prijavibug_text_input);
        prijaviBugBtn = findViewById(R.id.prijavibug_fab);


        onPrijaviBugClick();

    }

    void onPrijaviBugClick() {

        prijaviBugBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tekst = bugInputEditText.getText().toString();

                if(!TextUtils.isEmpty(tekst)) {
                    if(tekst.length() > 5 && tekst.length() <= 256 ) {

                        CustomToast.info(BugReportActivity.this, "Slanje prijave... ", Toast.LENGTH_SHORT).show();
                        prijaviBug(tekst);

                    }
                    else {
                        CustomToast.info(BugReportActivity.this, "Prijava mora biti kraća od 256 karaktera i duža od 5 !", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    CustomToast.info(BugReportActivity.this, "Morate popuniti tekst prijave !", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    void prijaviBug(String bugTekst) {

        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.GERMANY);

        Map<String, String> bugData = new HashMap<>();

        bugData.put("AutorID", user.getUid());
        bugData.put("ImeAutora", user.getDisplayName());
        bugData.put("Datum", dateFormat.format(date));
        bugData.put("BugTekst", bugTekst);




        db.collection("prijaveBugova").document().set(bugData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                CustomToast.uspjesno(BugReportActivity.this, "Prijava je uspješno poslata.", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CustomToast.error(BugReportActivity.this, "Došlo je do greške !", Toast.LENGTH_SHORT).show();
            }
        });


    }

}
