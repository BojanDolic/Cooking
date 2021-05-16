package com.electroniccode.cooking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.electroniccode.cooking.Adapteri.ReceptAdapter;
import com.electroniccode.cooking.Fragmenti.FilterFragment;
import com.electroniccode.cooking.klase.Recept;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ReceptAdapter.OnReceptClick,
        FilterFragment.OnReceptFilter {

    private DrawerLayout drawer;

    private RecyclerView recyclerView;
    private RecyclerView receptRecycler;

    FloatingActionButton createReceptFab;

    //SearchView receptSearch;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout noSearchResultLayout;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    SQLBazaNamirnica bazaNamirnica;
    private ReceptAdapter receptAdapter;

    private AdView adView;

    private static final String TAG = "MainActivity";

    String token = "";

    String documentPath = "";

    private byte kliknuoPuta = 0; // Za reklame
    private InterstitialAd interAd;
    private AdRequest interAdRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });

        adView = findViewById(R.id.banner_adview);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.d(TAG, "onAdFailedToLoad: ERROR UČITAVANJA OGLASA \n" +
                        "" +loadAdError.toString());
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });

        interAdRequest = new AdRequest.Builder().build();


        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, data -> {

                    Uri deeplink = null;
                    if(data != null) {
                        deeplink = data.getLink();
                        deeplink.getLastPathSegment();
                        Log.d(TAG, "onCreate: LINK " + deeplink.toString());
                        openReceptDetailsScreen("objaveKorisnika/" + deeplink.getLastPathSegment(), false);
                    }
                });



        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);


        noSearchResultLayout = findViewById(R.id.no_search_result_layout);
        receptRecycler = findViewById(R.id.main_recept_recycler);

        drawer = findViewById(R.id.drawer_layout_main);

        NavigationView navigationView = findViewById(R.id.nav_view_main);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open_drawer, R.string.close_drawer);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getRecepte();

        //getNamirniceData();

        createReceptFab = findViewById(R.id.create_recept_fab);

        createReceptFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, KreiranjeRecepta.class);

                Bundle extras = new Bundle();
                extras.putBoolean("KreiranjeReceptaAzuriranje", false);
                i.putExtras(extras);
                startActivity(i);
            }
        });


        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.orange));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                noSearchResultLayout.setVisibility(View.GONE);
                receptRecycler.setVisibility(View.VISIBLE);

                getRecepte();
            }
        });




    }


    private void getRecepte() {

        CollectionReference collectionReference = db.collection("objaveKorisnika");

        Query query = collectionReference.whereEqualTo("privatnaObjava", false).orderBy("datum", Query.Direction.DESCENDING);


        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(e != null && queryDocumentSnapshots != null) {

                    if (queryDocumentSnapshots.isEmpty()) {

                        receptRecycler.setVisibility(View.GONE);
                        noSearchResultLayout.setVisibility(View.VISIBLE);

                    } else {
                        receptRecycler.setVisibility(View.VISIBLE);
                        noSearchResultLayout.setVisibility(View.GONE);
                    }
                }
            }
        });



        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(5)
                .build();

        FirestorePagingOptions<Recept> opcije = new FirestorePagingOptions.Builder<Recept>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Recept.class)
                .build();


        receptAdapter = new ReceptAdapter(opcije, db);

        receptRecycler = findViewById(R.id.main_recept_recycler);
        receptRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        receptRecycler.setAdapter(receptAdapter);



        receptAdapter.setOnReceptClick(this);

        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        Intent i;

        if (id == R.id.odjavise_drawerbtn) {
            if (auth != null) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("korisnikovToken", "");

                db.collection("korisnici").document(user.getUid()).update(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Intent intent = new Intent(MainActivity.this, LoginRegisterActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        auth.signOut();
                        finish();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Intent intent = new Intent(MainActivity.this, LoginRegisterActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        auth.signOut();
                        finish();

                    }
                });
            }

                /*case R.id.myfridge_drawerbtn:
                i = new Intent(MainActivity.this, MyFridgeActivity.class);
                startActivity(i);
                break; */
        } else if (id == R.id.prijavibug_drawerbtn) {
            i = new Intent(MainActivity.this, BugReportActivity.class);
            startActivity(i);
        } else if (id == R.id.profil_drawerbtn) {
            i = new Intent(MainActivity.this, ProfilActivity.class);
            startActivity(i);
        } else if (id == R.id.zdrava_hrana_drawerbtn) {
            i = new Intent(MainActivity.this, ZdravaHrana.class);
            startActivity(i);
        } else if (id == R.id.tutorijal_drawerbtn) {
            i = new Intent(MainActivity.this, TutorijalKreiranjeRecepta.class);
            startActivity(i);

            /*case R.id.filtriraj_drawerBtn:
                FilterFragment filterFragment = new FilterFragment();
                filterFragment.show(getSupportFragmentManager(), "filterFrag");*/
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        MenuItem item = menu.findItem(R.id.recept_filter_icon);

        final Button filter = (Button) MenuItemCompat.getActionView(item);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        receptAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        receptAdapter.stopListening();
    }

    /**
     * Funkcija koja se poziva kada se klikne na recept card 5-ti put
     * Otvara oglas, a pri njegovom zatvaranju otvara novi ekran sa receptom
     */
    private void showAd() {
        if(interAd != null) {
            interAd.show(MainActivity.this);
            interstitialAdCallback();
        }
        else {
            openReceptDetailsScreen(documentPath, false);
        }

    }

    /**
     * Callback pomoću kojega upravljamo sta se desava pri akcijama nad oglasom
     */
    private void interstitialAdCallback() {
        interAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                openReceptDetailsScreen(documentPath, false);
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                openReceptDetailsScreen(documentPath, false);
            }

            @Override
            public void onAdShowedFullScreenContent() {
                interAd = null;
            }
        });
    }


    private void openReceptDetailsScreen(String path, Boolean profilEnter) {
        Intent i = new Intent(MainActivity.this, ReceptViewerActivity.class);
        i.putExtra("Document_PATH", path);
        i.putExtra("profilEnter", profilEnter);

        startActivity(i);
    }

    @Override
    public void onReceptClicked(DocumentSnapshot document, int position, ImageView slika) {

        InterstitialAd.load(
                this,
                "ca-app-pub-3485416724873570/8944979668",
                interAdRequest,
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

        if (document.exists()) {
            kliknuoPuta++;

            documentPath = document.getReference().getPath();

            if(kliknuoPuta >= 5) {
                showAd();
                kliknuoPuta = 0;
            } else {

                openReceptDetailsScreen(documentPath, false);

                Log.d(TAG, "onReceptClicked: " + document.getId());
            }

        } else {
            CustomToast.error(MainActivity.this, "Recept ne postoji, osvježite listu recepata !", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void getFilterResults(String kategorija, int tezinaPripreme, int vrijemePripreme) {

        CollectionReference collectionReference = db.collection("objaveKorisnika");

        Query query = collectionReference.whereEqualTo("privatnaObjava", false).orderBy("datum", Query.Direction.DESCENDING);


        if (!TextUtils.equals(kategorija, "Sve") && vrijemePripreme < 1 && tezinaPripreme < 1)
            query = QueryUtil.makeQuery(collectionReference, kategorija);
        else if (!TextUtils.equals(kategorija, "Sve") && vrijemePripreme > 0 && tezinaPripreme < 1)
            query = QueryUtil.makeQuery(collectionReference, kategorija, vrijemePripreme);
        else if (!TextUtils.equals(kategorija, "Sve") && vrijemePripreme > 0 && tezinaPripreme > 1 && tezinaPripreme <= 120)
            query = QueryUtil.makeQuery(collectionReference, kategorija, vrijemePripreme, tezinaPripreme);


        else if (TextUtils.equals(kategorija, "Sve") && vrijemePripreme > 0 && tezinaPripreme > 0 && tezinaPripreme <= 120)
            query = QueryUtil.makeQuery(collectionReference, vrijemePripreme, tezinaPripreme);
        else if (TextUtils.equals(kategorija, "Sve") && vrijemePripreme > 0 && tezinaPripreme < 1)
            query = QueryUtil.makeQuery(collectionReference, vrijemePripreme);

        else if (TextUtils.equals(kategorija, "Sve") && vrijemePripreme < 1 && tezinaPripreme > 0)
            query = QueryUtil.makeQueryTezina(collectionReference, tezinaPripreme);
        else if (!TextUtils.equals(kategorija, "Sve") && tezinaPripreme > 0 && vrijemePripreme < 1)
            query = QueryUtil.makeQueryKategorijaTezina(collectionReference, kategorija, tezinaPripreme);



        query.addSnapshotListener(MainActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(queryDocumentSnapshots != null) {

                    if (queryDocumentSnapshots.isEmpty()) {

                        receptRecycler.setVisibility(View.GONE);
                        noSearchResultLayout.setVisibility(View.VISIBLE);

                    } else {
                        receptRecycler.setVisibility(View.VISIBLE);
                        noSearchResultLayout.setVisibility(View.GONE);
                    }
                }
            }
        });


        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(5)
                .build();

        FirestorePagingOptions<Recept> opcije = new FirestorePagingOptions.Builder<Recept>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Recept.class)
                .build();


        receptAdapter = new ReceptAdapter(opcije, db);


        receptRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        receptRecycler.setAdapter(receptAdapter);

        receptAdapter.setOnReceptClick(this);

        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);


    }

    /**
     * Funkcija se poziva kada se pritisne ikonica filtriranja u toolbaru
     *
     * @param item
     */

    public void Filtriraj(MenuItem item) {
        FilterFragment filterFragment = new FilterFragment();
        filterFragment.show(getSupportFragmentManager(), "filterFrag");

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        kliknuoPuta = savedInstanceState.getByte("kliknuoPuta");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        outState.putByte("kliknuoPuta", kliknuoPuta);
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
