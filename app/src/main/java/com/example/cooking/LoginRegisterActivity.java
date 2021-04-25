package com.example.cooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.FloatArrayEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
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
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

public class LoginRegisterActivity extends AppCompatActivity {


    private TextInputEditText emailUnos, lozinkaUnos;
    private TextInputLayout emailUnosLayout, lozinkaUnosLayout;
    private Button loginBtn;
    private ImageView loginImage;
    private TextView logreg_text1, logreg_text2, register_activity_text;
    private boolean prijavljujeSe = false;


    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        Inicijalizacija();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


        auth.getAccessToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
            @Override
            public void onSuccess(GetTokenResult getTokenResult) {
                if (getTokenResult.getToken() != null && !TextUtils.isEmpty(getTokenResult.getToken())) {
                    startActivity(new Intent(LoginRegisterActivity.this, MainActivity.class));
                    finish();
                }
            }
        });



        final Korisnik korisnik = new Korisnik(user);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailUnos.getText().toString();
                String lozinka = lozinkaUnos.getText().toString();


                if (korisnik.CheckEmail(email)) {
                    if (korisnik.CheckLozinku(lozinka)) {

                        Login(email, lozinka);

                    } else {
                        Toast toast = Toast.makeText(LoginRegisterActivity.this, "Provjerite lozinku", Toast.LENGTH_SHORT);
                        //CustomToast.error(LoginRegisterActivity.this, "Provjerite lozinku!", Toast.LENGTH_SHORT);
                        InputError(lozinkaUnosLayout, toast);
                    }
                } else {
                    Toast toast = Toast.makeText(LoginRegisterActivity.this, "Provjerite E-mail", Toast.LENGTH_SHORT);
                    InputError(emailUnosLayout, toast);
                }

            }
        });

        register_activity_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginRegisterActivity.this, RegisterActivity.class));
            }
        });


        emailUnosLayout.setAlpha(0f);
        lozinkaUnosLayout.setAlpha(0f);
        loginBtn.setAlpha(0f);


        //region Animacije
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f);
        PropertyValuesHolder xSize = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f, 1f);
        PropertyValuesHolder ySize = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f, 1f);
        PropertyValuesHolder xBtn = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.5f, 1.0f);
        PropertyValuesHolder yBtn = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.5f, 1.0f);

        ObjectAnimator et2 = ObjectAnimator.ofPropertyValuesHolder(emailUnosLayout, alpha, xSize, ySize);
        ObjectAnimator et3 = ObjectAnimator.ofPropertyValuesHolder(lozinkaUnosLayout, alpha, xSize, ySize);

        ObjectAnimator loginText1 = ObjectAnimator.ofPropertyValuesHolder(logreg_text1, alpha);
        ObjectAnimator loginText2 = ObjectAnimator.ofPropertyValuesHolder(logreg_text2, alpha);

        ObjectAnimator btnAnim = ObjectAnimator.ofPropertyValuesHolder(loginBtn, xBtn, yBtn, alpha);

        loginText1.setDuration(1200);
        loginText1.setInterpolator(new LinearInterpolator());
        loginText2.setDuration(1400);

        et2.setDuration(900);
        et3.setDuration(1100);

        btnAnim.setDuration(700);
       /* et1.start();
        et2.start();
        et3.start(); */

        AnimatorSet set1 = new AnimatorSet();
        set1.playTogether(loginText1, loginText2);

        AnimatorSet set2 = new AnimatorSet();
        set2.playTogether(et2, et3);

        AnimatorSet set = new AnimatorSet();
        set.play(set1).before(set2);
        set.start();

        AnimatorSet btnSet = new AnimatorSet();
        btnSet.play(btnAnim).after(set);
        btnSet.start();

        //endregion


    }

    void Inicijalizacija() {

        emailUnos = findViewById(R.id.email_unos);
        lozinkaUnos = findViewById(R.id.lozinka_unos);

        emailUnosLayout = findViewById(R.id.email_unos_layout);
        lozinkaUnosLayout = findViewById(R.id.lozinka_unos_layout);

        loginImage = findViewById(R.id.login_heading);

        loginBtn = findViewById(R.id.logreg_btn);

        logreg_text1 = findViewById(R.id.logreg_text1);
        logreg_text2 = findViewById(R.id.logreg_text2);
        register_activity_text = findViewById(R.id.reg_activity_text);
    }


    void LogAndPlayLoginAnim() {

        PropertyValuesHolder headingRot = PropertyValuesHolder.ofFloat(View.ROTATION, 0.0f, 360.0f);

        ObjectAnimator headingAnim = ObjectAnimator.ofPropertyValuesHolder(loginImage, headingRot);
        headingAnim.setDuration(600);
        headingAnim.setInterpolator(new AccelerateInterpolator());


        AnimatorSet newActivitySet = new AnimatorSet();
        newActivitySet.play(headingAnim);
        newActivitySet.start();

        newActivitySet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                startActivity(new Intent(LoginRegisterActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    void Login(String email, String lozinka) {

        prijavljujeSe = true;

        CustomToast.info(LoginRegisterActivity.this, "Prijava u toku...", Toast.LENGTH_LONG).show();

        auth.signInWithEmailAndPassword(email, lozinka).addOnCompleteListener(LoginRegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    user = task.getResult().getUser();

                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {

                            if (task.isSuccessful()) {
                                String token = task.getResult().getToken();


                                Map<String, Object> userData = new HashMap<>();
                                userData.put("korisnikovToken", token);

                                db.collection("korisnici").document(user.getUid()).update(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        LogAndPlayLoginAnim();

                                        prijavljujeSe = false;

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        if(auth.getCurrentUser() != null)
                                            auth.signOut();

                                        prijavljujeSe = false;
                                    }
                                });

                            }
                            else
                            {
                                if(auth.getCurrentUser() != null)
                                    auth.signOut();

                                prijavljujeSe = false;
                            }
                        }
                    });

                } else if (!task.isSuccessful()) {
                    CustomToast.error(LoginRegisterActivity.this, "Došlo je do greške pri prijavljivanju !", Toast.LENGTH_LONG).show();
                    prijavljujeSe = false;
                }
            }
        });

    }

    void InputError(TextInputLayout layout, Toast toast) {

        PropertyValuesHolder layoutX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.1f, 1f, 1.1f, 1f);
        PropertyValuesHolder layoutY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.1f, 1f, 1.1f, 1f);

        ObjectAnimator layoutAnim = ObjectAnimator.ofPropertyValuesHolder(layout, layoutX, layoutY);
        layoutAnim.setDuration(1000);

        //layout.setErrorTextColor(ColorStateList.valueOf(Color.WHITE));
        //layout.setErrorIconTintList(ColorStateList.valueOf(Color.WHITE));

        if (toast != null)
            toast.show();

        layoutAnim.start();

    }


}
