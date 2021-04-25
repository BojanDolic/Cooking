package com.example.cooking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cooking.Adapteri.MojReceptViewPagerAdapter;
import com.example.cooking.util.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
import pub.devrel.easypermissions.EasyPermissions;

public class ProfilActivity extends AppCompatActivity implements PodesavanjeProfilaBottomSheet.SettingsBottomSheetListener {


    ImageView slikaKorisnika;

    TextView imeKorisnika;

    FloatingActionButton settingsFab;

    TabLayout profilTabLayout;
    ViewPager profilViewPager;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseStorage storage;


    androidx.appcompat.app.AlertDialog.Builder builder;
    androidx.appcompat.app.AlertDialog dialog;

    AlertDialog ucitavanjeDialog;

    String[] permisije = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    int GALERIJA_KOD = 15;

    private InterstitialAd interAd;
    private AdRequest adRequest;

    private static final String TAG = "ProfilActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.MaterialTheme);
        setContentView(R.layout.activity_profil);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        adRequest = new AdRequest.Builder().build();

        interAd = Utils.INSTANCE.loadInterAd(this, adRequest);


        slikaKorisnika = findViewById(R.id.profile_slika_korisnika);
        imeKorisnika = findViewById(R.id.profile_ime_korisnika);
        settingsFab = findViewById(R.id.profile_settings_fab);
        profilTabLayout = findViewById(R.id.profil_tab_layout);
        profilViewPager = findViewById(R.id.profile_viewpager);


        profilTabLayout.setSelectedTabIndicator(R.drawable.tab_layout_indicator_2);
        profilTabLayout.setSelectedTabIndicatorColor(getColor(R.color.colorPrimary));
        profilTabLayout.setTabIndicatorFullWidth(true);
        profilTabLayout.setSelectedTabIndicatorGravity(TabLayout.INDICATOR_GRAVITY_CENTER);
        profilTabLayout.setSelectedTabIndicatorHeight(56);
        profilTabLayout.setTabTextColors(Color.BLACK, Color.WHITE);
        profilTabLayout.setTabRippleColor(ColorStateList.valueOf(Color.TRANSPARENT));


        MojReceptViewPagerAdapter viewPagerAdapter = new MojReceptViewPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, db, user.getUid(), Glide.with(this));


        profilViewPager.setAdapter(viewPagerAdapter);

        profilTabLayout.setupWithViewPager(profilViewPager);



        Glide.with(this)
                .load(user.getPhotoUrl())
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.profile_icon_128)
                .into(slikaKorisnika);

        imeKorisnika.setText(user.getDisplayName());

        db.collection("korisnici").document(user.getUid()).addSnapshotListener(ProfilActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                imeKorisnika.setText(documentSnapshot.getString("imeKorisnika"));
            }
        });


        settingsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PodesavanjeProfilaBottomSheet bottomSheet = new PodesavanjeProfilaBottomSheet();
                bottomSheet.show(getSupportFragmentManager(), "podesavanjaBottomSheet");
            }
        });

    }

    @Override
    public void OnOptionClicked(String opcija) {

        if (TextUtils.equals(opcija, PodesavanjeProfilaBottomSheet.PROMJENA_IMENA_CODE)) {

            builder = new CustomInputDialog().createDialog(ProfilActivity.this,
                    "Trenutno ime je " + user.getDisplayName(),
                    "Unesite novo korisničko ime");

            View viewInflated = getLayoutInflater().inflate(R.layout.text_input_dialog_layout, (ViewGroup) findViewById(android.R.id.content), false);
            final TextInputEditText input = viewInflated.findViewById(R.id.text_input_dialog_edittext);

            final TextInputLayout layout = viewInflated.findViewById(R.id.text_input_dialog_textLayout);
            layout.setHint("Maksimalno 24 karaktera");

            builder.setView(viewInflated);

            dialog = builder.create();
            dialog.show();

            Button potvrdi = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button otkazi = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            potvrdi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String ime = input.getText().toString();

                    if (!TextUtils.isEmpty(ime)) {

                        if (ime.length() > 4) {
                            if (ime.length() < 25) {

                                ChangeUserName(ime, input);

                            } else layout.setError("Dužina imena ne može biti veća od 25 karaktera !");
                        } else layout.setError("Dužina imena ne može biti manja od 5 karaktera !");

                    } else layout.setError("Polje za unos ne može biti prazno !");
                }
            });

            otkazi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });


        }
        else if(TextUtils.equals(opcija, PodesavanjeProfilaBottomSheet.PROMJENA_SLIKE_CODE)) {

            CheckPermissionsAndOpenGallery(permisije);

        }


    }

    /**
     * Funkcija se poziva kada su svi uslovi za ažuriranje imena ispunjeni.
     * Nakon toga funkcija upisuje novo ime u korisnikov dokument na firestoru.
     */

    void ChangeUserName(final String ime, TextInputEditText inputEditText) {

        inputEditText.setEnabled(false);
        inputEditText.setHint("Učitavanje...");

        db.collection("korisnici").document(user.getUid()).update("imeKorisnika", ime).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


                UserProfileChangeRequest updateProfile = new UserProfileChangeRequest.Builder()
                        .setDisplayName(ime)
                        .build();

                user.updateProfile(updateProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        dialog.cancel();
                        CustomToast.uspjesno(ProfilActivity.this, "Vaše korisničko ime je promijenjeno", 0).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog.cancel();
                        CustomToast.error(ProfilActivity.this, "Došlo je do greške pri čuvanju korisničkog imena", 1).show();

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                dialog.cancel();
                CustomToast.error(ProfilActivity.this, "Došlo je do greške pri čuvanju korisničkog imena", 1).show();
            }
        });


    }


    void CheckPermissionsAndOpenGallery(String[] permisije) {

        if(EasyPermissions.hasPermissions(ProfilActivity.this, permisije)) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, GALERIJA_KOD);
        }
        else EasyPermissions.requestPermissions(ProfilActivity.this, "r", 10, permisije);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALERIJA_KOD) {
            if(resultCode == RESULT_OK) {
                if(data.getData() != null) {

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ProfilActivity.this);
                    dialogBuilder.setTitle("Ažuriranje profila...");
                    dialogBuilder.setCancelable(false);
                    //dialogBuilder.setView(R.layout.loading_dialog_layout);

                    View view = getLayoutInflater().inflate( R.layout.loading_dialog_layout, (ViewGroup) findViewById(android.R.id.content), false);
                    TextView text = view.findViewById(R.id.loading_dialog_text);
                    text.setText("Ažuriranje profilne slike.\nMolimo sačekajte");

                    dialogBuilder.setView(view);

                    ucitavanjeDialog = dialogBuilder.create();
                    ucitavanjeDialog.show();

                    Uri uri = data.getData();
                    File imageFile = null;

                    try {
                        String path = CameraFunctions.getImagePathFromUri(ProfilActivity.this, uri);
                        if(path != null)
                            imageFile = new Compressor(ProfilActivity.this).compressToFile(new File(path));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(imageFile != null) {

                        Glide.with(this)
                                .load(imageFile)
                                .override(128, 128)
                                .apply(RequestOptions.circleCropTransform())
                                .placeholder(R.drawable.slika_placeholder)
                                .into(slikaKorisnika);

                        UpdateUserImage(imageFile);

                    }
                    else CustomToast.error(ProfilActivity.this, "Greška pri odabiru slike !", 0).show();

                }
            }
        }
    }

    void UpdateUserImage(@NonNull File file) {

        final StorageReference storageReference = storage.getReference().child("profilne_slike/" + user.getUid());

        storageReference.putFile(Uri.fromFile(file)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(uri)
                                .build();

                        user.updateProfile(userProfileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Map<String, Object> updateProfila = new HashMap<>();
                                updateProfila.put("profilnaSlika", uri.toString());
                                updateProfila.put("lokacijaSlike",storageReference.getName());

                                db.collection("korisnici").document(user.getUid()).update(updateProfila).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        ucitavanjeDialog.cancel();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        ucitavanjeDialog.cancel();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ucitavanjeDialog.cancel();
                                CustomToast.error(ProfilActivity.this, "Greška pri ažuriranju profila !", 0).show();
                            }
                        });

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ucitavanjeDialog.cancel();
                CustomToast.error(ProfilActivity.this, "Greška pri postavljanju slike !", 0).show();
            }
        });

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();



        //if (dialog.isShowing() || dialog != null)
            //dialog.cancel();
    }

}
