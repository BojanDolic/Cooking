package com.electroniccode.cooking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.electroniccode.cooking.klase.Recept;
import com.electroniccode.cooking.klase.ReceptBuilder;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import id.zelory.compressor.Compressor;
import pub.devrel.easypermissions.EasyPermissions;

public class KreiranjeRecepta extends AppCompatActivity implements KreiranjeReceptaBottomSheet.ReceptBottomSheetListener {

    Button uploadEditButton, dodajSastojakBtn;
    ImageView slikaRecepta;

    TextInputEditText naslovRecepta;
    TextInputLayout naslovReceptaLayout;

    EditText brojOsoba_input, vrijemeIzrade_input;

    Spinner tezinaPripremeSpinner, vrstaObjaveSpinner, kategorijaReceptaSpinner;

    LinearLayout sastojciLayout, koraciLayout;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseStorage storage;

    EditText editText;
    int tezinaPripreme = 0;
    boolean azuriranje = false;
    boolean hasSliku = false;

    private Recept recept;

    private List<EditText> sastojci = new ArrayList<>();


    private String[] tezinaPripremeTexts = {"Lako", "Srednje", "Teško"};
    private String[] vrstaObjaveTexts = {"Javno", "Privatno"};
    private String[] kategorijeRecepta = {"Predjelo", "Gotovo jelo", "Supa/Čorba", "Riba", "Roštilj", "Salata", "Varivo", "Kompot", "Torta", "Kolač"};
    String kategorijaRecepta = "";
    boolean privatnaObjava = true;

    int receptiNum = 0;
    int koraciNum = 0;


    String Document_PATH;

    private static final String TAG = "KreiranjeRecepta";

    private final int KAMERA_KOD = 10;
    private final int GALERIJA_KOD = 20;

    String[] permisije = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};


    File globalFile;
    File selectedGalleryFile;
    //File slikaReceptaFajl;
    Uri slikaUri;
    String filePath;

    private InterstitialAd interAd;
    private AdRequest adRequest;


    final int permReqCode = 222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MaterialTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kreiranje_recepta);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        adRequest = new AdRequest.Builder().build();

        loadInterAd();

        if (savedInstanceState != null) {
            if (slikaUri == null && savedInstanceState.getString("slikaUri") != null)
                slikaUri = Uri.parse(savedInstanceState.getString("slikaUri"));
        }

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        Inicijalizuj();


        slikaRecepta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                KreiranjeReceptaBottomSheet bottomSheet = new KreiranjeReceptaBottomSheet();
                bottomSheet.show(getSupportFragmentManager(), "receptCreateBottomSheet");

            }
        });


        setupTezinaAdapter(tezinaPripremeSpinner);
        setupVrstaObjaveAdapter(vrstaObjaveSpinner);
        setupKategorijaReceptaSpinner(kategorijaReceptaSpinner);


        Intent i = getIntent();

        Bundle bundle = i.getExtras();

        if (bundle != null)
            CheckForAzuriranjeRecepta(bundle);


    }

    void Inicijalizuj() {

        uploadEditButton = findViewById(R.id.recept_upload_btn);
        naslovRecepta = findViewById(R.id.recept_naslov_recepta);
        sastojciLayout = findViewById(R.id.sastojci_linear_layout);
        dodajSastojakBtn = findViewById(R.id.dodaj_sastojak_btn);

        tezinaPripremeSpinner = findViewById(R.id.tezinaPripreme_spinner);
        vrstaObjaveSpinner = findViewById(R.id.vrstaObjave_spinner);
        kategorijaReceptaSpinner = findViewById(R.id.kategorijaJela_spinner);

        slikaRecepta = findViewById(R.id.kreiranje_recepta_slika);
        naslovReceptaLayout = findViewById(R.id.naslov_recepta_layout);

        brojOsoba_input = findViewById(R.id.broj_osoba_input);
        vrijemeIzrade_input = findViewById(R.id.vrijeme_pripreme_input);

        koraciLayout = findViewById(R.id.koraci_linear_layout);
    }


    int getSpinnerValueIndex(Spinner spinner, String naziv) {

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(naziv))
                return i;
        }
        return 0;

    }

    void CheckForAzuriranjeRecepta(Bundle bundle) {

        azuriranje = bundle.getBoolean("KreiranjeReceptaAzuriranje");

        if (azuriranje) {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle("Učitavanje recepta...");
            dialogBuilder.setCancelable(false);

            View v = getLayoutInflater().inflate(R.layout.loading_dialog_layout, (ViewGroup) findViewById(android.R.id.content), false);
            TextView text = v.findViewById(R.id.loading_dialog_text);
            text.setText("Vaš recept se učitava.\nMolimo sačekajte!");

            dialogBuilder.setView(v);

            final AlertDialog dialog = dialogBuilder.create();
            dialog.show();


            Document_PATH = bundle.getString("KreiranjeReceptaDocumentPath");

            if (Document_PATH != null) {

                db.document(Document_PATH).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {

                        if (snapshot.exists()) {

                            Recept recept = snapshot.toObject(Recept.class);


                            if (recept.getSlikaRecepta() != null && !recept.getSlikaRecepta().isEmpty()) {
                                Glide.with(KreiranjeRecepta.this)
                                        .load(recept.getSlikaRecepta())
                                        .into(slikaRecepta);

                                hasSliku = true;
                            }


                            naslovRecepta.setText(recept.getNaslovRecepta());
                            tezinaPripremeSpinner.setSelection(getSpinnerValueIndex(tezinaPripremeSpinner, recept.getTezinu(recept.getTezinaPripreme())));
                            vrstaObjaveSpinner.setSelection(getSpinnerValueIndex(vrstaObjaveSpinner, recept.convertVrstuObjave(recept.getPrivatnuObjavu())));
                            kategorijaReceptaSpinner.setSelection(getSpinnerValueIndex(kategorijaReceptaSpinner, recept.getKategorijaJela()));
                            brojOsoba_input.setText(String.valueOf(recept.getBrojOsoba()));
                            vrijemeIzrade_input.setText(String.valueOf(recept.getVrijemePripreme()));

                            for (int i = 0; i < recept.getSastojci().size(); i++) {
                                View v = getLayoutInflater().inflate(R.layout.sastojci_field, sastojciLayout, false);

                                sastojciLayout.addView(v, receptiNum++);

                                EditText editText = v.findViewById(R.id.sastojci_edittext);
                                editText.setText(recept.getSastojci().get(i));

                            }
                            //koraciNum = recept.getKoraci().size();
                            Log.d(TAG, "SVI KORACI: " + recept.getKoraci());
                            for (int i = 0; i < recept.getKoraci().size(); i++) {


                                View v = getLayoutInflater().inflate(R.layout.korak_field, koraciLayout, false);

                                koraciLayout.addView(v, koraciNum++);

                                EditText editText = (EditText) v.findViewById(R.id.korak_edittext);
                                editText.setText(recept.getKoraci().get(i));

                                //kor.add(editText);
                            }

                            uploadEditButton.setText("Ažuriraj");

                            dialog.dismiss();


                        } else {
                            dialog.dismiss();
                            finish();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog.dismiss();
                        finish();

                    }
                });


            }

        }

    }


    void setupKategorijaReceptaSpinner(Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(KreiranjeRecepta.this, R.layout.support_simple_spinner_dropdown_item, kategorijeRecepta);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                kategorijaRecepta = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    void setupTezinaAdapter(Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(KreiranjeRecepta.this, R.layout.support_simple_spinner_dropdown_item, tezinaPripremeTexts);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tezinaPripreme = ++i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    void setupVrstaObjaveAdapter(final Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(KreiranjeRecepta.this, R.layout.support_simple_spinner_dropdown_item, vrstaObjaveTexts);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 1:
                        privatnaObjava = true;
                        break;
                    case 0:
                        privatnaObjava = false;
                        break;
                    default:
                        privatnaObjava = true;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {

        if (slikaUri != null) {
            outState.putString("slikaUri", slikaUri.toString());
        }
        super.onSaveInstanceState(outState, outPersistentState);


    }

    //region Dodavanje i brisanje sastojka i koraka.

    // Služi za dodavanje jednog EditText-a, to jest sastojka.
    public void DodajSastojak(View view) {

        View v = getLayoutInflater().inflate(R.layout.sastojci_field, sastojciLayout, false);

        sastojciLayout.addView(v, receptiNum++);

        editText = (EditText) ((View) v.getParent()).findViewById(R.id.sastojci_edittext);

        sastojci.add(editText);

        PlayInflateAnim(v);

    }

    public void DodajKorak(View view) {

        View v = getLayoutInflater().inflate(R.layout.korak_field, koraciLayout, false);

        koraciLayout.addView(v, koraciNum++);

        //editText = (EditText) ((View) v.getParent()).findViewById(R.id.korak_edittext);

        //sastojci.add(editText);

        PlayInflateAnim(v);

    }

    //endregion

    public void RemoveSastojak(View view) {

        PlayRemoveAnim(view, sastojciLayout);
        receptiNum--;

    }

    public void RemoveKorak(View view) {

        PlayRemoveAnim(view, koraciLayout);
        koraciNum--;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {


            // Kada slika kamerom

            case KAMERA_KOD:
                if (resultCode == RESULT_OK) {

                    try {
                        globalFile = new Compressor(KreiranjeRecepta.this).compressToFile(globalFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    Glide.with(KreiranjeRecepta.this)
                            .load(globalFile)
                            .override(1280, 720)
                            .apply(RequestOptions.centerCropTransform().centerCrop())
                            .placeholder(R.drawable.slika_placeholder)
                            .into(slikaRecepta);

                }
                break;

            // Kada izabere da doda sliku iz galerije

            case GALERIJA_KOD:
                if (resultCode == RESULT_OK && data != null) {

                    //Uri pickedImageUri = (Uri) data.getParcelableExtra(FilePickerConst.KEY_SELECTED_MEDIA);

                    File imageFile = ImagePicker.Companion.getFile(data);

                    Uri uri = data.getData();

                    try {
                        globalFile = new Compressor(KreiranjeRecepta.this).compressToFile(imageFile);

                        Glide.with(this)
                                .load(globalFile)
                                .override(1280, 720)
                                .apply(RequestOptions.centerCropTransform().centerCrop())
                                .placeholder(R.drawable.slika_placeholder)
                                .into(slikaRecepta);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    Log.d(TAG, "onActivityResult: PATH GALERIJE  JE " + filePath);


                }
                break;
        }

    }


    /**
     * Ova funkcija je finalna pri kreiranju recepta
     * U njoj se formira recept i šalje na server
     *
     * @param storageReference referenca na kolekciju
     * @param slika slika koja se upload
     * @param naslovReceptaTxt naslov recepta
     * @param dialog dialog koji se prikazuje pri kreiranju
     * @param sastojci lista sastojaka
     * @param koraci lista koraka
     */


    void PublishRecept(@NonNull final StorageReference storageReference, @NonNull Uri slika, @NonNull final String naslovReceptaTxt,
                       final AlertDialog dialog, final List<String> sastojci, final List<String> koraci) {


        storageReference.putFile(slika).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        //downloadUrlString = task.getResult().toString();

                        //String imeRecepta = naslovRecepta.getText().toString();

                        if (tezinaPripreme != 0 && tezinaPripreme >= 1) {

                            //List<String> sastojci = getSastojke();

                            recept = new ReceptBuilder()
                                    .setNaslovRecepta(naslovReceptaTxt)
                                    .setAutor(user.getUid())
                                    .setSastojci(sastojci.size() > 0 ? sastojci : new ArrayList<>())
                                    .setKoraci(koraci.size() > 0 ? koraci : new ArrayList<>())
                                    .setTezinaPripreme(tezinaPripreme)
                                    .setPrivatnaObjava(privatnaObjava)
                                    .setBrojOsoba(getBrojOsoba(brojOsoba_input))
                                    .setVrijemePripreme(getVrijemePripreme(vrijemeIzrade_input))
                                    .setKategorijaRecepta(kategorijaRecepta)
                                    .setSlikaRecepta(task.getResult().toString())
                                    .setLokacijaSlike(storageReference.getName())
                                    .setBrojSvidjanja(0)
                                    .setBrojPrekrsaja(0)
                                    .setBrojPrijava(0)
                                    .setDatum(Timestamp.now())
                                    .createRecept();

                            /*final Map<String, Object> podaciRecepta = new HashMap<>();
                            //podaciRecepta.put("naslovRecepta", Arrays.asList(naslovReceptaTxt));
                            //podaciRecepta.put("datum", Timestamp.now());
                            //podaciRecepta.put("BrojSvidjanja", 0);
                            //podaciRecepta.put("brojPrijava", 0);
                            //podaciRecepta.put("brojPrekrsaja", 0);
                            //podaciRecepta.put("imeAutora", user.getUid());

                            if (sastojci.size() > 0)
                                podaciRecepta.put("sastojci", getSastojke());
                            else
                                podaciRecepta.put("sastojci", new ArrayList<String>());

                            if (koraci.size() > 0)
                                podaciRecepta.put("koraci", koraci);
                            else
                                podaciRecepta.put("koraci", new ArrayList<String>());

                            //podaciRecepta.put("tezinaPripreme", tezinaPripreme);
                            //podaciRecepta.put("privatnaObjava", privatnaObjava);
                            //podaciRecepta.put("brojOsoba", getBrojOsoba(brojOsoba_input));
                            //podaciRecepta.put("vrijemePripreme", getVrijemePripreme(vrijemeIzrade_input));
                            //podaciRecepta.put("kategorijaRecepta", kategorijaRecepta);
                            //podaciRecepta.put("lokacijaSlike", storageReference.getName());


                            try {
                                podaciRecepta.put("slikaRecepta", task.getResult().toString());
                            } catch (NullPointerException e) {
                                podaciRecepta.put("slikaRecepta", "");
                            }*/

                            db.collection("objaveKorisnika/").add(recept).addOnCompleteListener(task1 -> {

                                if (task1.isSuccessful()) {

                                    String id = task1.getResult().getId();

                                    db.collection("korisnici").document(user.getUid()).update("objavljeniRecepti", FieldValue.arrayUnion(id)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task1) {

                                            dialog.cancel();
                                            showInterAd();


                                        }
                                    });


                                }

                            });
                        }

                    }
                });

            }
        });

    }

    /**
     * Funkcija se poziva ako je KreiranjeRecepta activity pozvan iz ProfilActivity-a
     * Funkcija provjerava sve stvari u vezi unesenih podataka i ažurira recept na serveru
     */

    void AzurirajRecept() {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Ažuriranje recepta...");
        dialogBuilder.setCancelable(false);

        View v = getLayoutInflater().inflate(R.layout.loading_dialog_layout, (ViewGroup) findViewById(android.R.id.content), false);
        TextView text = v.findViewById(R.id.loading_dialog_text);
        text.setText("Vaš recept se ažurira.\nMolimo sačekajte.");

        dialogBuilder.setView(v);

        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        /////////////////////////////////////////////////////////

        String naslovReceptaText = naslovRecepta.getText().toString();


        List<String> sastojci = getSastojke();
        List<String> koraci = getKorake();

        int brojOsoba = getBrojOsoba(brojOsoba_input);
        int vrijemePripreme = getVrijemePripreme(vrijemeIzrade_input);

        final Map<String, Object> updateData = new HashMap<>();

        if (!privatnaObjava) {

            boolean valuesChecked = CheckInputFields(naslovRecepta, brojOsoba_input, vrijemeIzrade_input);

            updateData.put("naslovRecepta", Arrays.asList(naslovReceptaText));
            updateData.put("privatnaObjava", privatnaObjava);

            if (sastojci.size() < 1 || koraci.size() < 1 || !valuesChecked) {//|| brojOsoba == 0 || vrijemePripreme == 0) {
                CustomToast.info(KreiranjeRecepta.this, "Provjerite unesene podatke !", 0).show();
                dialog.dismiss();
                return;
            } else {
                updateData.put("sastojci", sastojci);
                updateData.put("koraci", koraci);
                updateData.put("brojOsoba", brojOsoba);
                updateData.put("vrijemePripreme", vrijemePripreme);
            }

            updateData.put("tezinaPripreme", tezinaPripreme);
            updateData.put("kategorijaRecepta", kategorijaRecepta);

            if (globalFile != null) {
                Calendar cal = Calendar.getInstance();

                final StorageReference docRef = storage.getReference().child("receptiSlike/img_" + cal.getTimeInMillis());

                docRef.putFile(Uri.fromFile(globalFile)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        docRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                updateData.put("slikaRecepta", uri.toString());
                                updateData.put("lokacijaSlike", docRef.getName());

                                db.document(Document_PATH).update(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        CustomToast.uspjesno(KreiranjeRecepta.this, "Vaš recept je uspješno ažuriran !", 0).show();
                                        dialog.cancel();
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                    }
                                });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                    }
                });

            } else if (hasSliku) {
                db.document(Document_PATH).update(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        CustomToast.uspjesno(KreiranjeRecepta.this, "Vaš recept je uspješno ažuriran !", 0).show();
                        dialog.cancel();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                    }
                });

            } else {
                CustomToast.error(KreiranjeRecepta.this, "Morate postaviti sliku ako želite da recept bude dostupan svima !", 1).show();
                dialog.dismiss();
                return;
            }
        } else {
            updateData.put("naslovRecepta", Arrays.asList(naslovReceptaText));
            updateData.put("privatnaObjava", privatnaObjava);

            if (sastojci.size() > 0)
                updateData.put("sastojci", sastojci);
            else
                updateData.put("sastojci", new ArrayList<String>());

            if (koraci.size() > 0)
                updateData.put("koraci", koraci);
            else
                updateData.put("koraci", new ArrayList<String>());

            updateData.put("brojOsoba", brojOsoba);
            updateData.put("vrijemePripreme", vrijemePripreme);

            updateData.put("tezinaPripreme", tezinaPripreme);
            updateData.put("kategorijaRecepta", kategorijaRecepta);

            if (globalFile != null) {

                Calendar cal = Calendar.getInstance();

                final StorageReference docRef = storage.getReference().child("receptiSlike/img_" + cal.getTimeInMillis());

                docRef.putFile(Uri.fromFile(globalFile)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        docRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                updateData.put("slikaRecepta", uri.toString());
                                updateData.put("lokacijaSlike", docRef.getName());

                                db.document(Document_PATH).update(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        CustomToast.uspjesno(KreiranjeRecepta.this, "Vaš recept je uspješno ažuriran !", 0).show();
                                        dialog.cancel();
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                CustomToast.error(KreiranjeRecepta.this, "Greška pri ažuriranju slike !", 0).show();
                                dialog.dismiss();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CustomToast.error(KreiranjeRecepta.this, "Greška pri ažuriranju slike !", 0).show();
                        dialog.dismiss();
                    }
                });

            } else {
                db.document(Document_PATH).update(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        CustomToast.uspjesno(KreiranjeRecepta.this, "Vaš recept je uspješno ažuriran !", 0).show();
                        dialog.cancel();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                    }
                });
            }


        }

    }

    /**
     * Funkcija se poziva kada korisnik odabere dugme "Objavi",
     * funkcija provjerava sve stvari vezane za recept i ako sve provjere prodju,
     * Recept se šalje na Firebase
     */

    //region Funkcija ObjaviRecept
    public void ObjaviRecept(View view) throws IOException {

        if (azuriranje) {
            AzurirajRecept();
        } else {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle("Kreiranje recepta...");
            dialogBuilder.setCancelable(false);
            dialogBuilder.setView(R.layout.loading_dialog_layout);


            // Ako objava nije privatna šalje na server gdje je recept dostupan svim korisnicima.
            if (!privatnaObjava) {

                boolean valuesChecked = CheckInputFields(naslovRecepta, brojOsoba_input, vrijemeIzrade_input); // Ne mijenjati raspored input-a

                final AlertDialog dialog = dialogBuilder.create();

                if (valuesChecked) {

                    if (globalFile != null) {

                        dialog.show();


                        String imeRecepta = naslovRecepta.getText().toString();

                        Calendar cal = Calendar.getInstance();

                        FirebaseStorage storage = FirebaseStorage.getInstance();

                        StorageReference reference = storage.getReference();

                        final StorageReference receptiSlike = reference.child("receptiSlike/img_" + cal.getTimeInMillis());

                        Uri slika = Uri.fromFile(globalFile);

                        List<String> sastojci = getSastojke();
                        List<String> koraci = getKorake();

                        if (sastojci.size() < 1 || koraci.size() < 1) {

                            CustomToast.error(KreiranjeRecepta.this, "Sastojci i koraci ne smiju biti prazni !", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            return;
                        }


                        PublishRecept(receptiSlike, slika, imeRecepta, dialog, sastojci, koraci);

                    } else {
                        CustomToast.error(KreiranjeRecepta.this, "Morate odabrati sliku recepta !", Toast.LENGTH_LONG).show();
                    }

                } else {
                    CustomToast.error(KreiranjeRecepta.this, "Provjerite unesene podatke !", Toast.LENGTH_LONG).show();
                }
            } else {

                // Ako recept jeste privatan, šalje ga na server, ali je recept dostupan samo autoru.

                final AlertDialog dialog = dialogBuilder.create();

                SacuvajRecept(dialog);

            }
        }
    }
    //endregion


    /**
     * Ova funkcija se poziva kada korisnik odabere vrstuObjave "Privatno"
     * Ona automatski čuva korisnikovu objavu na Firestore i stavlja oznaku Privatno
     * Što znači da ona neće biti prikazana ostalim korisnicima.
     * Korisnik ima opciju izmjene sačuvanog recepta kroz Profil meni.
     */

    void SacuvajRecept(@NonNull final AlertDialog dialog) {

        // Treba sačuvati recept kod korisnika u fajl

        CollectionReference korisniciRef = db.collection("korisnici");
        final DocumentReference korisnikovDokument = korisniciRef.document(user.getUid());//db.document(user.getUid());

        final CollectionReference objaveKorisnikaCollection = db.collection("objaveKorisnika");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        //StorageReference reference = storage.getReference();

        final String naslovReceptaText = naslovRecepta.getText().toString();

        final List<String> sastojci = getSastojke();
        final List<String> koraci = getKorake();

        final int brojOsoba = getBrojOsoba(brojOsoba_input);
        final int vrijemePripreme = getVrijemePripreme(vrijemeIzrade_input);

        if (!TextUtils.isEmpty(naslovReceptaText)) {

            dialog.show();

            if (globalFile == null) {


                final Map<String, Object> podaciRecepta = new HashMap<>();

                podaciRecepta.put("naslovRecepta", Arrays.asList(naslovReceptaText));
                podaciRecepta.put("privatnaObjava", privatnaObjava);
                podaciRecepta.put("BrojSvidjanja", 0);
                podaciRecepta.put("imeAutora", user.getUid());

                if (sastojci.size() > 0)
                    podaciRecepta.put("sastojci", getSastojke());
                else
                    podaciRecepta.put("sastojci", new ArrayList<String>());

                if (koraci.size() > 0)
                    podaciRecepta.put("koraci", koraci);
                else
                    podaciRecepta.put("koraci", new ArrayList<String>());

                podaciRecepta.put("tezinaPripreme", tezinaPripreme);
                if (brojOsoba != 0)
                    podaciRecepta.put("brojOsoba", brojOsoba);

                if (vrijemePripreme != 0)
                    podaciRecepta.put("vrijemePripreme", getVrijemePripreme(vrijemeIzrade_input));

                podaciRecepta.put("kategorijaRecepta", kategorijaRecepta);
                podaciRecepta.put("slikaRecepta", "");


                objaveKorisnikaCollection.add(podaciRecepta).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {


                        korisnikovDokument.update("objave", FieldValue.arrayUnion(documentReference.getId())).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                CustomToast.uspjesno(KreiranjeRecepta.this, "Recept je uspješno sačuvan !", 0).show();

                                showInterAd();

                                //finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                    }
                });


            } else {


                Calendar cal = Calendar.getInstance();

                final StorageReference docRef = storage.getReference().child("receptiSlike/img_" + cal.getTimeInMillis());

                docRef.putFile(Uri.fromFile(globalFile)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        docRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                final Map<String, Object> podaciRecepta = new HashMap<>();

                                podaciRecepta.put("naslovRecepta", Arrays.asList(naslovReceptaText));
                                podaciRecepta.put("privatnaObjava", privatnaObjava);
                                podaciRecepta.put("BrojSvidjanja", 0);
                                podaciRecepta.put("imeAutora", user.getUid());

                                if (sastojci.size() > 0)
                                    podaciRecepta.put("sastojci", getSastojke());
                                else
                                    podaciRecepta.put("sastojci", new ArrayList<String>());

                                if (koraci.size() > 0)
                                    podaciRecepta.put("koraci", koraci);
                                else
                                    podaciRecepta.put("koraci", new ArrayList<String>());

                                podaciRecepta.put("tezinaPripreme", tezinaPripreme);
                                if (brojOsoba != 0)
                                    podaciRecepta.put("brojOsoba", brojOsoba);

                                if (vrijemePripreme != 0)
                                    podaciRecepta.put("vrijemePripreme", getVrijemePripreme(vrijemeIzrade_input));

                                podaciRecepta.put("slikaRecepta", uri.toString());
                                podaciRecepta.put("kategorijaRecepta", kategorijaRecepta);
                                podaciRecepta.put("lokacijaSlike", docRef.getName());

                                objaveKorisnikaCollection.add(podaciRecepta).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {


                                        korisnikovDokument.update("objave", FieldValue.arrayUnion(documentReference.getId())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                showInterAd();
                                                dialog.dismiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                    }
                                });

                            }
                        });

                    }
                });
            }
        } else {
            CustomToast.info(KreiranjeRecepta.this, "Morate unijeti ime recepta !", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }

    }

    private void loadInterAd() {
        InterstitialAd.load(
                this,
                "ca-app-pub-3485416724873570/8944979668",
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        interAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        interAd = null;
                        //openReceptDetailsScreen(documentPath, false);
                    }
                });
    }

    private void showInterAd() {
        if (interAd != null) {
            interAd.show(KreiranjeRecepta.this);

            interAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    finish();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    interAd = null;
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    finish();
                }
            });

        } else finish();
    }

    void PlayInflateAnim(View view) {
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1.0f);
        PropertyValuesHolder sizeY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.2f, 1.0f);
        PropertyValuesHolder sizeX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f, 0.9f, 1f);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, alpha, sizeY, sizeX);
        animator.setValues(sizeX, sizeY);
        animator.setDuration(700);
        animator.start();
    }

    void PlayRemoveAnim(View v, final LinearLayout layout) {

        final View ViewToBeRemoved = v;

        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f);
        PropertyValuesHolder sizeX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, 300f);

        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(v.getParent(), alpha, sizeX);
        anim.setDuration(450);

        AnimatorSet set = new AnimatorSet();
        set.play(anim);
        set.start();

        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                layout.removeView((View) ViewToBeRemoved.getParent());
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });


    }


    //region Dobijanje sastojaka i koraka iz EditTextova

    private List<String> getSastojke() {

        List<String> sastojci = new ArrayList<>();

        View temp;
        for (int i = 0; i < sastojciLayout.getChildCount(); i++) {
            temp = sastojciLayout.getChildAt(i);
            if (temp instanceof ConstraintLayout) {

                EditText et = temp.findViewById(R.id.sastojci_edittext);
                if (!et.getText().toString().isEmpty())
                    sastojci.add(et.getText().toString());
            }
        }

        return sastojci;
    }

    private List<String> getKorake() {

        List<String> koraci = new ArrayList<>();

        View temp;
        for (int i = 0; i < koraciLayout.getChildCount(); i++) {
            temp = koraciLayout.getChildAt(i);
            if (temp instanceof ConstraintLayout) {
                EditText et = temp.findViewById(R.id.korak_edittext);
                if (!et.getText().toString().isEmpty())
                    koraci.add(et.getText().toString());
            }
        }

        return koraci;
    }

    //endregion

    @Override
    public void OnOptionClicked(String opcija) {

        if (TextUtils.equals(opcija, "otvoriKameru")) {
            OtvoriKameru();
        } else if (TextUtils.equals(opcija, "otvoriGaleriju")) {
            OtvoriGaleriju();
        } else {
            CustomToast.error(KreiranjeRecepta.this, "Greška pri otvaranju galerije/kamere", Toast.LENGTH_LONG).show();
        }

    }


    void OtvoriKameru() {

        if (EasyPermissions.hasPermissions(this, permisije)) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (intent.resolveActivity(getPackageManager()) != null) {


                File tempFile = null;

                try {

                    tempFile = createImageFile();

                } catch (IOException e) {
                    e.printStackTrace();
                }


                if (tempFile != null) {
                    Uri slikaUri = FileProvider.getUriForFile(KreiranjeRecepta.this, "fileProvider", tempFile);

                    globalFile = tempFile;

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, slikaUri);
                    startActivityForResult(intent, KAMERA_KOD);
                }

            }

        } else {
            EasyPermissions.requestPermissions(this, "R", permReqCode, permisije);

        }

    }

    void OtvoriGaleriju() {

        if (EasyPermissions.hasPermissions(this, permisije)) {

            /*FilePickerBuilder.getInstance()
                    .pickPhoto(this, GALERIJA_KOD);*/

            ImagePicker.Companion.with(this)
                    .galleryOnly()
                    .start(GALERIJA_KOD);

            /*Intent intent = new Intent(Intent.ACTION_PICK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            //intent.putExtra(MediaStore.EXTRA_OUTPUT, slikaUri);
            startActivityForResult(intent, GALERIJA_KOD);*/

        } else {
            EasyPermissions.requestPermissions(this, "R", permReqCode, permisije);
        }
    }

    File getFile(String imeFajla) {

        File odrediste = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "slikaTag");

        if (!odrediste.exists() && !odrediste.mkdirs()) {
            Log.d(TAG, "getFile: Greška pri kreiranju fajla");
        }

        return new File(odrediste.getPath() + File.separator + imeFajla);
    }


    private File createImageFile() throws IOException {

        String time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + time + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        //filePath = image.getAbsolutePath();

        return image;
    }

//////////////////////////////


    private String getImagePathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};

        //Cursor cursor = context.managedQuery(uri, projection, null, null, null);
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        try {
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(index);

        } catch (NullPointerException e) {
            CustomToast.error(KreiranjeRecepta.this, e.getMessage(), Toast.LENGTH_LONG).show();
            cursor.close();
            return null;
        }

        //return null;
    }

    private String getFilePath(Intent data) {
        return getImageFromPath(data);
    }

    private String getImageFromPath(Intent data) {

        boolean cam = data == null || data.getData() == null;

        if (cam)
            return getCaptureImageOutputUri().getPath();
        else return getImagePathFromUri(data.getData());
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File imgFolder = new File(Environment.getExternalStorageDirectory(), "cookit/temp");

        if (!imgFolder.exists())
            imgFolder.mkdirs();


        outputFileUri = FileProvider.getUriForFile(KreiranjeRecepta.this, "fileProvider", imgFolder);
        return outputFileUri;
    }

    private boolean CheckInputFields(TextInputEditText naslov, EditText brojOsoba, EditText vrijemeIzrade) {

        boolean okay = false;

        String naslovText = naslov.getText().toString();
        String brojOsobaText = brojOsoba.getText().toString();
        String vrijemeIzradeText = vrijemeIzrade.getText().toString();

        if (!TextUtils.isEmpty(naslovText) && !TextUtils.isEmpty(brojOsobaText) && !TextUtils.isEmpty(vrijemeIzradeText))
            if (naslovText.length() < 26 && (Integer.parseInt(brojOsobaText) > 0 && Integer.parseInt(brojOsobaText) < 15)
                    && (Integer.parseInt(vrijemeIzradeText) > 4 && Integer.parseInt(vrijemeIzradeText) <= 120))
                okay = true;
        return okay;
    }


    private int getBrojOsoba(EditText brojOsobaEditText) {
        if (!TextUtils.isEmpty(brojOsobaEditText.getText().toString()))
            return Integer.parseInt(brojOsobaEditText.getText().toString());
        else return 0;
    }

    private int getVrijemePripreme(EditText vrijemePripremeEditText) {
        if (!TextUtils.isEmpty(vrijemePripremeEditText.getText().toString()))
            return Integer.parseInt(vrijemePripremeEditText.getText().toString());
        else return 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


}
