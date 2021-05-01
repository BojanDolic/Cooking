package com.electroniccode.cooking;

import com.google.firebase.firestore.PropertyName;

import java.util.List;

public class Recept {

    @PropertyName("naslovRecepta")
    private List<String> naslovRecepta;
    private String imeAutora;
    private String slikaAutora;
    private String slikaRecepta;
    private String vrstaObjave;
    private String kategorijaRecepta;


    private List<String> koraci;
    private List<String> sastojci;

    private int vrijemePripreme;
    private int tezinaPripreme;
    private int brojOsoba;
    private int BrojSvidjanja;
    private boolean privatnaObjava;



    public Recept() {
    }

    public Recept(List<String> naslovRecepta, String imeAutora, String slikaAutora, String slikaRecepta, boolean privatnaObjava, List<String> koraci,
                  List<String> sastojci, int vrijemePripreme, int tezinaPripreme, int brojOsoba, int BrojSvidjanja,
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
        this.BrojSvidjanja = BrojSvidjanja;
        this.kategorijaRecepta = kategorijaRecepta;
    }



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

    public int getBrojSvidjanja() {
        return BrojSvidjanja;
    }

    public void setBrojSvidjanja(int brojSvidjanja) {
        this.BrojSvidjanja = brojSvidjanja;
    }

    public List<String> getNaslovRecepta() {
        return naslovRecepta;
    }

    public void setNaslovRecepta(List<String> naslovRecepta) {
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


}
