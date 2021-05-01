package com.electroniccode.cooking;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SQLBazaNamirnica extends SQLiteOpenHelper {

    private static final String Ime_Baze = "baza_namirnica";
    private static final String IME_TABELE = "tabela_namirnica";
    private static final String ID_KOLONA = "id";
    private static final String NAMIRNICA_KOLONA = "namirnica";
    private static int VERZIJA_BAZE = 5;

    private List<String> namirnice = new ArrayList<>();

    SQLiteDatabase db;

    Context context;

    public SQLBazaNamirnica(Context context, int ver) {

        super(context, Ime_Baze, null, ver);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.db = sqLiteDatabase;

        String kreirajTabelu = "CREATE TABLE " + IME_TABELE +
                " (" + ID_KOLONA + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAMIRNICA_KOLONA + " TEXT )";

        db.execSQL(kreirajTabelu);

    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int staraV, int novaV) {

        //VERZIJA_BAZE++;
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + IME_TABELE);
            KreirajTabelu();
            updateBazu((ArrayList<String>) namirnice);
            Log.d("ONUPDATE SQL", "onUpdate je pozvan");

    }

    boolean CheckVerziju(SQLBazaNamirnica db, int novaVer) {

        //db = getWritableDatabase();

        SQLiteDatabase _db = this.getWritableDatabase();

        Log.d("SQL VERZIJA", "CheckVerziju: VERZIJA BAZE: " + _db.getVersion() + " \n VERZIJA FIREBASE BAZE: " + novaVer);



        if(_db.getVersion() != novaVer)
        {
            //VERZIJA_BAZE = novaVer;
            _db.setVersion(novaVer);
            return true;
        } else return false;
    }

    void KreirajTabelu() {

        //SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + IME_TABELE);


        String kreirajTabelu = "CREATE TABLE " + IME_TABELE +
                " (" + ID_KOLONA + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                 NAMIRNICA_KOLONA + " TEXT )";

        db.execSQL(kreirajTabelu);

        Log.d("SQL", "KreirajTabelu: BAZA JE KREIRANA");

    }

    public void getListOfNamirnice(ArrayList<String> namirnice) {
        this.namirnice = namirnice;
    }

    void updateBazu(ArrayList<String> namirnice) {

        db = getWritableDatabase();

        ContentValues cv = new ContentValues();

        for(String namirnica : namirnice) {
           /* String query = "INSERT INTO " + IME_TABELE + " (" +
                    NAMIRNICA_KOLONA + ") VALUES (" + namirnica + ")"; */

            cv.put(NAMIRNICA_KOLONA, namirnica);

            db.insert(IME_TABELE, null, cv);
        }
    }

    ArrayList<String> filterNamirnice(String filter) {

        // Treba napraviti filtriranje baze za ispis u MyFridge

        return null;
    }

}
