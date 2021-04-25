package com.example.cooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText korisnickoImeUnos, emailUnos, lozinkaUnos;
    private TextInputLayout korisnickoImeUnosLayout, emailUnosLayout, lozinkaUnosLayout;
    private Button regButton;
    private ImageView headingImage;
    private TextView register_text1, register_text2;


    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;

    Anim anim;

    boolean registrujeSe = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        final Korisnik korisnik = new Korisnik();

        Inicijalizacija();

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!registrujeSe) {

                    String email = emailUnos.getText().toString();
                    String lozinka = lozinkaUnos.getText().toString();
                    String korisnickoIme = korisnickoImeUnos.getText().toString();

                    if (korisnik.CheckEmail(email)) {
                        if (korisnik.CheckLozinku(lozinka)) {
                            if (korisnik.CheckKorisnickoIme(korisnickoIme)) {

                                RegistrujKorisnika(email, lozinka, korisnickoIme);

                            } else {
                                Toast text = Toast.makeText(RegisterActivity.this, "Provjerite korisničko ime !", Toast.LENGTH_LONG);
                                anim.InputError(korisnickoImeUnosLayout, text);
                            }
                        } else {
                            Toast text = Toast.makeText(RegisterActivity.this, "Provjerite lozinku !", Toast.LENGTH_LONG);
                            anim.InputError(lozinkaUnosLayout, text);
                        }
                    } else {
                        Toast text = Toast.makeText(RegisterActivity.this, "Provjerite E-mail !", Toast.LENGTH_LONG);
                        anim.InputError(emailUnosLayout, text);
                    }

                }
            }
        });

        PlayAnims();


    }

    void PlayAnims() {

        korisnickoImeUnosLayout.setAlpha(0f);
        emailUnosLayout.setAlpha(0f);
        lozinkaUnosLayout.setAlpha(0f);
        regButton.setAlpha(0f);


        //region Animacije

        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f);
        PropertyValuesHolder xSize = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f, 1f);
        PropertyValuesHolder ySize = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f, 1f);
        PropertyValuesHolder xBtn = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.5f, 1.0f);
        PropertyValuesHolder yBtn = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.5f, 1.0f);

        ObjectAnimator et1 = ObjectAnimator.ofPropertyValuesHolder(korisnickoImeUnosLayout, alpha, xSize, ySize);
        ObjectAnimator et2 = ObjectAnimator.ofPropertyValuesHolder(emailUnosLayout, alpha, xSize, ySize);
        ObjectAnimator et3 = ObjectAnimator.ofPropertyValuesHolder(lozinkaUnosLayout, alpha, xSize, ySize);

        ObjectAnimator loginText1 = ObjectAnimator.ofPropertyValuesHolder(register_text1, alpha);
        ObjectAnimator loginText2 = ObjectAnimator.ofPropertyValuesHolder(register_text2, alpha);

        ObjectAnimator btnAnim = ObjectAnimator.ofPropertyValuesHolder(regButton, xBtn, yBtn, alpha);

        loginText1.setDuration(1200);
        loginText1.setInterpolator(new LinearInterpolator());
        loginText2.setDuration(1400);

        et1.setDuration(700);
        et2.setDuration(900);
        et3.setDuration(1100);

        btnAnim.setDuration(700);
       /* et1.start();
        et2.start();
        et3.start(); */

        AnimatorSet set1 = new AnimatorSet();
        set1.playTogether(loginText1, loginText2);

        AnimatorSet set2 = new AnimatorSet();
        set2.playTogether(et1, et2, et3);

        AnimatorSet set = new AnimatorSet();
        set.play(set1).before(set2);
        set.start();

        AnimatorSet btnSet = new AnimatorSet();
        btnSet.play(btnAnim).after(set);
        btnSet.start();

        //endregion


    }

    void Inicijalizacija() {

        anim = new Anim();


        korisnickoImeUnosLayout = findViewById(R.id.korisnickoime_unoslayout_register);
        emailUnosLayout = findViewById(R.id.email_unos_register_layout);
        lozinkaUnosLayout = findViewById(R.id.lozinka_unos_register_layout);

        register_text1 = findViewById(R.id.register_text1);
        register_text2 = findViewById(R.id.register_text2);

        korisnickoImeUnos = findViewById(R.id.korisnickoime_unos_register);
        emailUnos = findViewById(R.id.email_unos_register);
        lozinkaUnos = findViewById(R.id.lozinka_unos_register);

        regButton = findViewById(R.id.register_btn);

    }


    void RegistrujKorisnika(String email, String lozinka, final String ime) {


        Toast.makeText(RegisterActivity.this, "Registracija u toku...", Toast.LENGTH_LONG).show();

        registrujeSe = true;

        auth.createUserWithEmailAndPassword(email, lozinka).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    user = auth.getCurrentUser();


                    UserProfileChangeRequest updateProfile = new UserProfileChangeRequest.Builder()
                            .setDisplayName(ime)
                            .build();


                    user.updateProfile(updateProfile).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                                        if (task.isSuccessful()) {
                                            String token = task.getResult().getToken();


                                            Map<String, Object> userData = new HashMap<>();
                                            userData.put("imeKorisnika", user.getDisplayName());
                                            userData.put("korisnikovToken", token);

                                            db.collection("korisnici").document(user.getUid()).set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    Toast.makeText(RegisterActivity.this, "Registracija uspješna", Toast.LENGTH_LONG).show();

                                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                    finish();

                                                    registrujeSe = false;

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(RegisterActivity.this, "Greška pri upisivanju podataka u bazu!", Toast.LENGTH_LONG).show();

                                                    registrujeSe = false;
                                                }
                                            });

                                        }
                                        else
                                        {
                                            Toast.makeText(RegisterActivity.this, "Greška pri registraciji", Toast.LENGTH_LONG).show();
                                            registrujeSe = false;
                                        }
                                    }
                                });


                            }

                        }
                    });

                } else {
                    Toast.makeText(RegisterActivity.this, "Greška pri registraciji", Toast.LENGTH_LONG).show();
                    registrujeSe = false;
                }
            }
        });

    }

}
