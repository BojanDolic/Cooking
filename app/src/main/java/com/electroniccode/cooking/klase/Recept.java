package com.electroniccode.cooking.klase;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.List;

public class Recept {

    @PropertyName("naslovRecepta")
    private String naslovRecepta;
    private String imeAutora;
    @Exclude private String slikaAutora;
    private String slikaRecepta;
    private String kategorijaRecepta;
    private String lokacijaSlike;
    private Timestamp datum;


    private List<String> koraci;
    private List<String> sastojci;

    private int vrijemePripreme;
    private int tezinaPripreme;
    //private final long brojSvidjanja;
    private int brojOsoba;
    private int brojPrijava;
    private int brojPrekrsaja;
    private long brojSvidjanja;
    private boolean privatnaObjava;


    public Recept() { }

    /*public Recept(String naslovRecepta, String imeAutora, String slikaAutora, String slikaRecepta, boolean privatnaObjava, List<String> koraci, List<String> sastojci, int vrijemePripreme, int tezinaPripreme, int brojOsoba, long brojSvidjanja, String kategorijaRecepta, Timestamp datum, String autor, String lokacijaSlike, int brojPrijava, int brojPrekrsaja) {
    }*/

    /*public Recept(String naslovRecepta, String imeAutora, String slikaAutora, String slikaRecepta, boolean privatnaObjava, List<String> koraci,
                  List<String> sastojci, int vrijemePripreme, int tezinaPripreme, int brojOsoba, long BrojSvidjanja,
                  String kategorijaRecepta) {
        this.naslovRecepta = naslovRecepta;
        this.imeAutora = imeAutora;
        this.slikaAutora = slikaAutora;
        this.slikaRecepta = slikaRecepta;
        this.privatnaObjava = privatnaObjava;
        this.koraci = koraci;
        this.sastojci = sastojci;
        this.vrijemePripreme = vrijemePripreme;
        this.tezinaPripreme = tezinaPripreme;
        this.brojOsoba = brojOsoba;
        this.brojSvidjanja = BrojSvidjanja;
        this.kategorijaRecepta = kategorijaRecepta;
    }*/

   /* public Recept(
            String naslovRecepta,
            String kategorijaRecepta,
            String autor,
            String slikaRecepta,
            Timestamp datum,
            String lokacijaSlike,
            List<String> sastojci,
            List<String> koraci,
            boolean privatnaObjava,
            int vrijemePripreme,
            int brojOsoba,
            int tezinaPripreme,
            long brojSvidjanja,
            int brojPrijava,
            int brojPrekrsaja
            ) {
        this.naslovRecepta = naslovRecepta;
        this.kategorijaRecepta = kategorijaRecepta;
        this.imeAutora = autor;
        this.slikaRecepta = slikaRecepta;
        this.datum = datum;
        this.lokacijaSlike = lokacijaSlike;
        this.sastojci = sastojci;
        this.koraci = koraci;
        this.privatnaObjava = privatnaObjava;
        this.vrijemePripreme = vrijemePripreme;
        this.brojOsoba = brojOsoba;
        this.tezinaPripreme = tezinaPripreme;
        this.brojSvidjanja = brojSvidjanja;
        this.brojPrijava = brojPrijava;
        this.brojPrekrsaja = brojPrekrsaja;

    }*/

    public Recept(String naslovRecepta, String imeAutora, String slikaRecepta,  String kategorijaRecepta, String lokacijaSlike, Timestamp datum, List<String> koraci, List<String> sastojci, int vrijemePripreme, int tezinaPripreme, int brojOsoba, int brojPrijava, int brojPrekrsaja, long brojSvidjanja, boolean privatnaObjava) {
        this.naslovRecepta = naslovRecepta;
        this.imeAutora = imeAutora;
        this.slikaRecepta = slikaRecepta;
        this.kategorijaRecepta = kategorijaRecepta;
        this.lokacijaSlike = lokacijaSlike;
        this.datum = datum;
        this.koraci = koraci;
        this.sastojci = sastojci;
        this.vrijemePripreme = vrijemePripreme;
        this.tezinaPripreme = tezinaPripreme;
        this.brojOsoba = brojOsoba;
        this.brojPrijava = brojPrijava;
        this.brojPrekrsaja = brojPrekrsaja;
        this.brojSvidjanja = brojSvidjanja;
        this.privatnaObjava = privatnaObjava;
    }

    /*public Recept(String naslovRecepta, String imeAutora, String slikaRecepta, boolean privatnaObjava, List<String> koraci, List<String> sastojci, int vrijemePripreme, int tezinaPripreme, int brojOsoba, long brojSvidjanja, String kategorijaRecepta, Timestamp datum, String autor, String lokacijaSlike, int brojPrijava, int brojPrekrsaja) {


    }*/


    //region Getter i setter metode


    @PropertyName("sastojci")
    public List<String> getSastojci() {
        return sastojci;
    }

    @PropertyName("koraci")
    public List<String> getKoraci() {
        return koraci;
    }

    @PropertyName("kategorijaRecepta")
    public String getKategorijaJela() {
        return kategorijaRecepta;
    }

    public void setKategorijaJela(String kategorijaJela) {
        this.kategorijaRecepta = kategorijaJela;
    }

    public String getSlikaRecepta() {
        return slikaRecepta;
    }

    public void setSlikaRecepta(String slikaRecepta) {
        this.slikaRecepta = slikaRecepta;
    }

    @PropertyName("privatnaObjava")
    public boolean getPrivatnuObjavu() {
        return privatnaObjava;
    }

    public int getBrojOsoba() {
        return brojOsoba;
    }

    public void setBrojOsoba(int brojOsoba) {
        this.brojOsoba = brojOsoba;
    }

    public long getBrojSvidjanja() {
        return brojSvidjanja;
    }

    public void setBrojSvidjanja(long brojSvidjanja) {
        this.brojSvidjanja = brojSvidjanja;
    }

    public String getNaslovRecepta() {
        return naslovRecepta;
    }

    public void setNaslovRecepta(String naslovRecepta) {
        this.naslovRecepta = naslovRecepta;
    }

    public String getImeAutora() {
        return imeAutora;
    }

    public void setImeAutora(String imeAutora) {
        this.imeAutora = imeAutora;
    }

    public String getSlikaAutora() {
        return slikaAutora;
    }

    public void setSlikaAutora(String slikaAutora) {
        this.slikaAutora = slikaAutora;
    }

    public int getVrijemePripreme() {
        return vrijemePripreme;
    }

    public void setVrijemePripreme(int vrijemePripreme) {
        this.vrijemePripreme = vrijemePripreme;
    }

    public int getTezinaPripreme() {
        return tezinaPripreme;
    }

    public void setTezinaPripreme(int tezinaPripreme) {
        this.tezinaPripreme = tezinaPripreme;
    }

    //endregion


    public String getTezinu(int tezina) {

        switch (tezina) {
            case 1: return "Lako";
            case 2: return "Srednje";
            case 3: return "Teško";
            default: return "N/A";
        }

    }

    public String convertVrstuObjave(boolean privatnaObjava) {

        if(privatnaObjava) {
            return "Privatno";
        }
        else {
            return "Javno";
        }
    }

    String getKategoriju(int kategorija) {

        switch (kategorija) {
            case 1: return "Slano jelo";
            case 2: return "Slatko jelo";
            default: return "N/A";
        }
    }


    public Timestamp getDatum() {
        return datum;
    }

    public void setDatum(Timestamp datum) {
        this.datum = datum;
    }

    public String getLokacijaSlike() {
        return lokacijaSlike;
    }

    public void setLokacijaSlike(String lokacijaSlike) {
        this.lokacijaSlike = lokacijaSlike;
    }

    public int getBrojPrekrsaja() {
        return brojPrekrsaja;
    }

    public void setBrojPrekrsaja(int brojPrekrsaja) {
        this.brojPrekrsaja = brojPrekrsaja;
    }

    public int getBrojPrijava() {
        return brojPrijava;
    }

    public void setBrojPrijava(int brojPrijava) {
        this.brojPrijava = brojPrijava;
    }
}
