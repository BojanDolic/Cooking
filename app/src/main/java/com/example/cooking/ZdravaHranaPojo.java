package com.example.cooking;

import com.google.firebase.firestore.PropertyName;

import java.util.List;
import java.util.Map;

public class ZdravaHranaPojo {

    private String ime;
    private String uvod;
    private String slika;


    private List<String> naslovi;
    private List<String> tekstovi;


    public ZdravaHranaPojo() {
    }

    public ZdravaHranaPojo(String ime, String slika, String uvod, Map<String, String> zaglavlje,
                           List<String> naslovi, List<String> tekstovi) {
        this.ime = ime;
        this.slika = slika;
        this.uvod = uvod;
        this.naslovi = naslovi;
        this.tekstovi = tekstovi;

    }

    @PropertyName("slika")
    public String getSlika() {
        return slika;
    }

    public void setSlika(String slika) {
        this.slika = slika;
    }

    @PropertyName("ime")
    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    @PropertyName("uvod")
    public String getUvod() {
        return uvod;
    }

    public void setUvod(String uvod) {
        this.uvod = uvod;
    }

    public List<String> getNaslovi() {
        return naslovi;
    }

    public List<String> getTekstovi() {
        return tekstovi;
    }

}
