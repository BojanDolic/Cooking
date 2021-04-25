package com.example.cooking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.TextUtils;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cooking.Adapteri.KomentarAdapter;
import com.example.cooking.Adapteri.ReceptViewPagerAdapter;
import com.example.cooking.util.Utils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReceptViewerActivity extends AppCompatActivity implements AddCommentDialog.NoviKomentarListener, KomentarAdapter.OnKomentarClicked {


    private static final String TAG = "ReceptViewerActivity";

    String Document_PATH;
    String AutorID;
    //private static String ReceptID;

    Toolbar toolbar;

    TextView tezinaPripreme, brojOsoba, vrijemePripreme, ocjenaRecepta;

    FloatingActionButton likeReceptFab;
    Button dodajKomentarBtn;

    TextView naslovRecepta;
    ImageView slikaRecepta;

    ViewPager viewPager;
    TabLayout tabLayout;

    BottomSheetBehavior bottomSheetBehavior;
    RecyclerView komentariRecycler;
    RelativeLayout bottomSheetDragger;
    LinearLayout komentariEmptyLayout;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseStorage storage;
    FirebaseFirestore db;
    CollectionReference collection;
    CollectionReference komentariCollection;
    CollectionReference lajkoviCollection;

    Recept trenutniRecept;

    KomentarAdapter komentarAdapter;
    AddCommentDialog dialogComment;

    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;

    private static String receptID;

    boolean lajkovao = false;
    boolean updatingFabState = false;
    boolean privatnaObjava = false;

    private long zadnjiClick = 0;

    boolean enteredFromProfil = false;

    private AdRequest adRequest;
    private InterstitialAd interAd;

    /// Privremeno za komentare
    List<String> komentari = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.MaterialThemeWithStatusBar);
        setContentView(R.layout.activity_recept_viewer);

        MobileAds.initialize(this);

        adRequest = new AdRequest.Builder().build();

        interAd = Utils.INSTANCE.loadInterAd(this, adRequest);


        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Učitavanje recepta...");
        dialogBuilder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.loading_dialog_layout, (ViewGroup) findViewById(android.R.id.content), false);

        TextView text = view.findViewById(R.id.loading_dialog_text);
        text.setText("Učitavanje recepta...\nMolimo sačekajte !");

        dialogBuilder.setView(view);

        dialog = dialogBuilder.create();


        dialog.show();

        getWindow().setExitTransition(new Explode());
        getWindow().setEnterTransition(new Explode());

        toolbar = findViewById(R.id.recept_viewer_toolbar);
        setSupportActionBar(toolbar);
        toolbar.getOverflowIcon().setTint(Color.WHITE);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        naslovRecepta = findViewById(R.id.naslov_recepta_viewer);
        slikaRecepta = findViewById(R.id.slika_recepta_viewer);

        tezinaPripreme = findViewById(R.id.recept_viewer_tezinaPripremeText);
        brojOsoba = findViewById(R.id.recept_viewer_brojOsoba);
        vrijemePripreme = findViewById(R.id.recept_viewer_vrijemePripreme);
        ocjenaRecepta = findViewById(R.id.recept_viewer_ocjena);

        likeReceptFab = findViewById(R.id.recept_like_fab);
        dodajKomentarBtn = findViewById(R.id.dodaj_komentar_btn);

        komentariRecycler = findViewById(R.id.komentari_recycler);

        bottomSheetDragger = findViewById(R.id.bottom_sheet_dragger);
        komentariEmptyLayout = findViewById(R.id.komentari_empty_layout);


        viewPager = findViewById(R.id.recept_viewer_viewpager);
        tabLayout = findViewById(R.id.recept_viewer_tabLayout);

        setUpTabLayout(tabLayout);

        // Bottom sheet komentari
        View cardBottomSheet = findViewById(R.id.komentari_bottom_sheet);

        bottomSheetBehavior = BottomSheetBehavior.from(cardBottomSheet);


        Intent i = getIntent();

        Bundle bundle = i.getExtras();
        try {
            Document_PATH = bundle.getString("Document_PATH");
            lajkoviCollection = db.document(Document_PATH).collection("Sviđanja");

            enteredFromProfil = bundle.getBoolean("profilEnter");

        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: " + e.getMessage());
        }


        // Provjera da li je korisnik već lajkovao ovaj recept

        user = auth.getCurrentUser();


        collection = db.collection("objaveKorisnika");
        komentariCollection = db.document(Document_PATH).collection("komentari");

        db.document(Document_PATH).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {


                if (documentSnapshot.exists()) {
                    receptID = documentSnapshot.getId();
                    AutorID = documentSnapshot.getString("imeAutora");

                    trenutniRecept = documentSnapshot.toObject(Recept.class);

                    privatnaObjava = trenutniRecept.getPrivatnuObjavu();
                    if (privatnaObjava)
                        dodajKomentarBtn.setVisibility(View.GONE);

                    if (trenutniRecept != null)
                        LoadData(trenutniRecept);


                    if (!TextUtils.equals(AutorID, user.getUid())) {

                        likeReceptFab.setVisibility(View.VISIBLE);


                        lajkoviCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                List<String> likedUserIDs = new ArrayList<>();

                                for (DocumentSnapshot document : queryDocumentSnapshots) {

                                    if (document.exists())
                                        likedUserIDs.add(document.getId());

                                }

                                for (String s : likedUserIDs) {

                                    if (TextUtils.equals(s, user.getUid())) {
                                        likeReceptFab.setImageResource(R.drawable.srce_full_icon);
                                        likeReceptFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e32424")));
                                        lajkovao = true;
                                        return;
                                    } else {

                                        likeReceptFab.setBackgroundResource(R.drawable.srce_empty_icon);
                                        likeReceptFab.setSupportBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e32424")));
                                        lajkovao = false;
                                    }
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                likeReceptFab.setVisibility(View.GONE);
                            }
                        });

                    } else {
                        likeReceptFab.setVisibility(View.GONE);
                    }
                } else {
                    dialog.cancel();
                    finish();
                }
            }
        });

        //////////////////////////////////////////////////////////////////////


        db.document(Document_PATH).addSnapshotListener(ReceptViewerActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                //if (e != null && documentSnapshot != null) {
                    if (documentSnapshot.exists()) {
                        ocjenaRecepta.setText(String.format(Locale.getDefault(), "%d", documentSnapshot.getLong("BrojSvidjanja")));
                        if(documentSnapshot.getBoolean("privatnaObjava") && !enteredFromProfil) {
                            CustomToast.info(getApplicationContext(), "Recept je sklonjen od strane servera ili skriven od strane autora !", 1).show();
                            new CountDownTimer(2000, 1000) {

                                @Override
                                public void onTick(long millisUntilFinished) {

                                }

                                @Override
                                public void onFinish() {
                                    finish();
                                }
                            }.start();
                        }
                    } else {
                        CustomToast.error(getApplicationContext(), "Recept je uklonjen sa servera !", 1).show();
                        new CountDownTimer(2000, 1000) {

                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                finish();
                            }
                        }.start();
                    }
                //}
            }
        });


        // Privremeno dobijanje komentara radi testa
        getAllKomentare(komentariCollection);

        onReceptLikeClickListener();


        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        bottomSheetDragger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                else
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });


        dodajKomentarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otvoriCommentDialog();
            }
        });


    }

    void setUpTabLayout(TabLayout tabLayout) {

        tabLayout.setSelectedTabIndicator(R.drawable.tab_layout_indicator_2);
        tabLayout.setSelectedTabIndicatorColor(getColor(R.color.colorPrimary));
        tabLayout.setTabIndicatorFullWidth(true);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setSelectedTabIndicatorGravity(TabLayout.INDICATOR_GRAVITY_CENTER);
        tabLayout.setSelectedTabIndicatorHeight(56);
        tabLayout.setTabTextColors(Color.BLACK, Color.WHITE);
        tabLayout.setTabRippleColor(ColorStateList.valueOf(Color.TRANSPARENT));


    }

    void otvoriCommentDialog() {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        Fragment prevFrag = getSupportFragmentManager().findFragmentByTag("commentDialog");

        if (prevFrag != null)
            fragmentTransaction.remove(prevFrag);

        fragmentTransaction.addToBackStack(null);

        dialogComment = new AddCommentDialog();
        dialogComment.show(fragmentTransaction, "commentDialog");

    }

    void getAllKomentare(CollectionReference collectionReference) {


        Query query = komentariCollection;

        query.addSnapshotListener(ReceptViewerActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                    if (queryDocumentSnapshots.isEmpty()) {
                        komentariRecycler.setVisibility(View.GONE);
                        komentariEmptyLayout.setVisibility(View.VISIBLE);
                    } else {
                        komentariRecycler.setVisibility(View.VISIBLE);
                        komentariEmptyLayout.setVisibility(View.GONE);
                    }
            }
        });


        FirestoreRecyclerOptions<Komentar> opcije = new FirestoreRecyclerOptions.Builder<Komentar>()
                .setQuery(query, Komentar.class)
                .build();


        komentarAdapter = new KomentarAdapter(opcije, db, Glide.with(this));

        komentarAdapter.setOnKomentarClickListener(this);

        komentariRecycler.setAdapter(komentarAdapter);
        komentariRecycler.setLayoutManager(new LinearLayoutManager(this));

    }

    void onReceptLikeClickListener() {



            likeReceptFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(SystemClock.elapsedRealtime() - zadnjiClick < 1000) {
                        return;
                    }

                    zadnjiClick = SystemClock.elapsedRealtime();

                    user = auth.getCurrentUser();

                    final DocumentReference document = db.document(Document_PATH);

                    if (!updatingFabState) {

                        db.runTransaction(new Transaction.Function<Void>() {

                            @Nullable
                            @Override
                            public Void apply(@NonNull final Transaction transaction) throws FirebaseFirestoreException {

                                DocumentSnapshot snapshot = transaction.get(document);

                                //if (!updatingFabState) {

                                if (lajkovao) {

                                    updatingFabState = true;

                                    final long brojSvidjanja = snapshot.getLong("BrojSvidjanja") - 1;

                                    transaction.update(document, "BrojSvidjanja", brojSvidjanja);

                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("lajkovaniRecepti", FieldValue.arrayRemove(document.getId()));

                                    lajkoviCollection.document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {


                                            likeReceptFab.setImageResource(R.drawable.srce_empty_icon);
                                            likeReceptFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e32424")));

                                            lajkovao = false;
                                            updatingFabState = false;

                                            //updatingFabState = false;
                                        }
                                    });


                                } else {

                                    updatingFabState = true;

                                    final long brojSvidjanja = snapshot.getLong("BrojSvidjanja") + 1;

                                    transaction.update(document, "BrojSvidjanja", brojSvidjanja);

                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("korisnikovID", user.getUid());

                                    lajkoviCollection.document(user.getUid()).set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            likeReceptFab.setImageResource(R.drawable.srce_full_icon);
                                            likeReceptFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e32424")));

                                            lajkovao = true;
                                            updatingFabState = false;


                                        }
                                    });

                                }


                                return null;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: Transakcija uspješna");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: Transakcija neuspješna");
                            }
                        });

                    }

                }

            });

    }

    void LoadData(Recept recept) {

        Glide.with(this)
                .load(recept.getSlikaRecepta())
                .override(1366, 720)
                .into(slikaRecepta);


        naslovRecepta.setText(recept.getNaslovRecepta().get(0));

        tezinaPripreme.setText(recept.getTezinu(recept.getTezinaPripreme()));
        brojOsoba.setText(String.format(Locale.getDefault(), "%d", recept.getBrojOsoba()));
        vrijemePripreme.setText(String.format(Locale.getDefault(), "%d min", recept.getVrijemePripreme()));
        ocjenaRecepta.setText(String.format(Locale.getDefault(), "%d", recept.getBrojSvidjanja()));

        List<String> sastojci = recept.getSastojci();
        List<String> koraci = recept.getKoraci();

        viewPager.setAdapter(new ReceptViewPagerAdapter(
                getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                sastojci != null ? sastojci : new ArrayList<String>(),
                koraci != null ? koraci : new ArrayList<String>()));

        tabLayout.setupWithViewPager(viewPager);

        dialog.dismiss();

    }


    @Override
    public void onBackPressed() {


        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else
            super.onBackPressed();


    }

    @Override
    protected void onStart() {
        super.onStart();
        komentarAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        komentarAdapter.stopListening();
    }

    @Override
    public void getKomentarText(String tekst) {


        user = auth.getCurrentUser();

        Map<String, Object> komentarData = new HashMap<>();
        komentarData.put("komentar", tekst);
        komentarData.put("imeAutora", user.getUid());
        //komentarData.put();

        db.document(Document_PATH).collection("komentari").document().set(komentarData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                dialogComment.dismiss();
                CustomToast.uspjesno(ReceptViewerActivity.this, "Komentar uspješno objavljen !", 0).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialogComment.dismiss();
                CustomToast.error(ReceptViewerActivity.this, "Greška pri objavljivanju komentara !", 0).show();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recept_menu, menu);


        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.recept_ispravka_teksta_prijava:

                if (!TextUtils.equals(AutorID, user.getUid())) {

                    androidx.appcompat.app.AlertDialog.Builder builder;

                    builder = new CustomInputDialog().createDialog(ReceptViewerActivity.this,
                            "Prijavite grešku u tekstu",
                            "Opišite gdje ste pronašli grešku");


                    View viewInflated = getLayoutInflater().inflate(R.layout.text_input_dialog_layout, (ViewGroup) findViewById(android.R.id.content), false);
                    final TextInputEditText input = viewInflated.findViewById(R.id.text_input_dialog_edittext);

                    final TextInputLayout layout = viewInflated.findViewById(R.id.text_input_dialog_textLayout);
                    layout.setHint("Maksimalno 100 karaktera");


                    builder.setView(viewInflated);

                    final androidx.appcompat.app.AlertDialog dialog = builder.create();
                    dialog.show();


                    final Button posaljiBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    final Button otkaziBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                    posaljiBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final String text = input.getText().toString();

                            if (text.length() <= 100 && text.length() > 4) {
                                if (!TextUtils.isEmpty(text)) {

                                    //input.setEnabled(false);
                                    posaljiBtn.setEnabled(false);
                                    otkaziBtn.setEnabled(false);
                                    posaljiBtn.setText("Slanje prijave...");

                                    db.document(Document_PATH).collection("prijave").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                            boolean imaPrijavu = false;

                                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {

                                                if (TextUtils.equals(snapshot.getId(), user.getUid())) {
                                                    imaPrijavu = true;
                                                    break;
                                                } else imaPrijavu = false;

                                            }


                                            if (imaPrijavu) {
                                                dialog.dismiss();
                                                CustomToast.error(getApplicationContext(), getResources().getText(R.string.greška_prijava_recepta_vec_prijavljen), 0).show();

                                            } else {
                                                SendPrijavuNaServer(text, dialog, input);
                                            }

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog.dismiss();
                                            CustomToast.error(getApplicationContext(), "Greška pri prijavi recepta !", 0).show();
                                        }
                                    });

                                } else layout.setError("Polje za unos ne može biti prazno");


                            } else
                                layout.setError("Uneseni tekst ne može biti duži od 100 karaktera i kraći od 5 karaktera !");
                        }
                    });


                    otkaziBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialog.cancel();
                        }
                    });
                } else
                    CustomToast.error(getApplicationContext(), getResources().getText(R.string.greška_prijava_recepta_svoj), 0).show();


                break;


            /*
             * AKO KORISNIK IZABERE NEPRIMJEREN SADRŽAJ
             */


            case R.id.recept_neprimjeren_sadrzaj_prijava:

                if (!TextUtils.equals(AutorID, user.getUid())) {

                    androidx.appcompat.app.AlertDialog.Builder neprimjerenSadrzajDialogBuilder;

                    neprimjerenSadrzajDialogBuilder = CustomInputDialog.createYesNoDialog(ReceptViewerActivity.this,
                            "Prijavite neprimjeren sadržaj",
                            "Ako želite prijaviti neprimjeren sadržaj, na primjer neprimjerenu sliku recepta, pritisnite PRIJAVI");

                    final androidx.appcompat.app.AlertDialog nepSadrzajDialog = neprimjerenSadrzajDialogBuilder.create();
                    nepSadrzajDialog.show();

                    final Button prijaviBtn = nepSadrzajDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    final Button odustaniBtn = nepSadrzajDialog.getButton(AlertDialog.BUTTON_NEGATIVE);


                    prijaviBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            prijaviBtn.setEnabled(false);
                            odustaniBtn.setEnabled(false);

                            db.document(Document_PATH).collection("prijave").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                    boolean imaPrijavu = false;

                                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {

                                        if (TextUtils.equals(snapshot.getId(), user.getUid())) {
                                            imaPrijavu = true;
                                            break;
                                        }

                                    }


                                    if (imaPrijavu) {
                                        nepSadrzajDialog.dismiss();
                                        CustomToast.error(getApplicationContext(), getResources().getText(R.string.greška_prijava_recepta_vec_prijavljen), 0).show();

                                    } else

                                        SendPrijavuNaServer("Neprimjeren sadržaj", nepSadrzajDialog, null);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    nepSadrzajDialog.dismiss();
                                    CustomToast.error(getApplicationContext(), "Greška pri prijavi recepta !", 0).show();
                                }
                            });
                        }
                    });

                } else
                    CustomToast.error(getApplicationContext(), getResources().getText(R.string.greška_prijava_recepta_svoj), 0).show();


                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
    }


    void SendPrijavuNaServer(String tekst, final androidx.appcompat.app.AlertDialog dialog, final TextInputEditText inputEditText) {

        if (inputEditText != null)
            inputEditText.setEnabled(false);


        Map<String, Object> prijava = new HashMap<>();
        prijava.put("imeKorisnika", user.getDisplayName());
        prijava.put("datum", Timestamp.now());
        prijava.put("korisnikovUID", user.getUid());
        prijava.put("tekstPrijave", tekst);

        db.document(Document_PATH).collection("prijave").document(user.getUid()).set(prijava).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    if(interAd != null)
                        interAd.show(ReceptViewerActivity.this);

                    final DocumentReference document = db.document(Document_PATH);

                    db.runTransaction(new Transaction.Function<Void>() {


                        @Nullable
                        @Override
                        public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                            DocumentSnapshot snapshot = transaction.get(document);

                            final int brojPrijava = snapshot.getLong("brojPrijava").intValue() + 1;

                            transaction.update(document, "brojPrijava", brojPrijava);

                            return null;
                        }


                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CustomToast.uspjesno(ReceptViewerActivity.this, "Prijava je uspješno poslata!", 0).show();

                            if (inputEditText != null)
                                inputEditText.setEnabled(true);

                            dialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CustomToast.error(ReceptViewerActivity.this, "Došlo je do greške pri slanju prijave !", 0).show();

                            if (inputEditText != null)
                                inputEditText.setEnabled(true);

                            dialog.dismiss();
                        }
                    });

                } else {
                    CustomToast.error(ReceptViewerActivity.this, "Došlo je do greške pri slanju prijave !", 0).show();
                    if (inputEditText != null)
                        inputEditText.setEnabled(true);

                    dialog.dismiss();
                }
            }
        });

    }

    @Override
    public void onKomentarLongClick(DocumentSnapshot snapshott) {

        db.document(snapshott.getReference().getPath()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot snapshot) {

                if (snapshot.exists()) {

                    String userID = snapshot.getString("imeAutora");

                    if (TextUtils.equals(user.getUid(), userID)) {

                       androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ReceptViewerActivity.this)
                                .setTitle("Želite izbrisati komentar?")
                                .setMessage("Klikom na opciju obriši vaš komentar će biti uklonjen!")
                                .setPositiveButton("Obriši", null)
                                .setNegativeButton("Otkaži", null);

                        final androidx.appcompat.app.AlertDialog dialog = builder.create();
                        dialog.show();

                        Button obrisiBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        obrisiBtn.setTextColor(getColor(R.color.crvena));

                        obrisiBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                db.document(snapshot.getReference().getPath()).delete();
                                dialog.dismiss();
                            }
                        });

                        Button otkaziBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        otkaziBtn.setTextColor(getColor(R.color.zelenaUspjesno));

                    }
                }

            }
        });

    }
}
