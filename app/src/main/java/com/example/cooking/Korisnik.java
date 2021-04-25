package com.example.cooking;

import android.service.autofill.CharSequenceTransformation;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.regex.Pattern;

public class Korisnik {

    private static final String TAG = "Korisnik";

    boolean logged = false;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore database;

    String imeKorisnika;

    private static final Korisnik ourInstance = new Korisnik();

    public static Korisnik getInstance() {
        return ourInstance;
    }

    public Korisnik() {}

    public Korisnik(FirebaseFirestore database) {
        this.database = database;
    }

    public Korisnik(FirebaseUser user) {
        this.user = user;
    }

    public Korisnik(FirebaseUser user, FirebaseAuth auth, FirebaseFirestore database) {

        this.auth = auth;
        this.user = user;
        this.database = database;

    }




    public boolean LoginCheck() {

        if(user != null) {


            String token = user.getIdToken(true).toString();
            if(token != null)
                logged = true;

        }
        return logged;
    }


    public boolean CheckEmail(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()); }


    public boolean CheckLozinku(String lozinka) {
        return (!TextUtils.isEmpty(lozinka) && lozinka.length() < 16 && lozinka.length() > 4); }

        public boolean CheckKorisnickoIme(String ime) {
            return (ime.length() > 4 && ime.length() < 25 && !TextUtils.isEmpty(ime));
        }
}
