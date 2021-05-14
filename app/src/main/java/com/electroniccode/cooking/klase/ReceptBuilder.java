package com.electroniccode.cooking.klase;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceptBuilder {
    private String naslovRecepta = "";
    private String imeAutora = "";
    private String slikaRecepta = "";
    private boolean privatnaObjava;
    private List<String> koraci = new ArrayList<>();
    private List<String> sastojci = new ArrayList<>();
    private int vrijemePripreme;
    private int tezinaPripreme;
    private int brojOsoba;
    private long brojSvidjanja;
    private String kategorijaRecepta = "";
    private String autor;
    private Timestamp datum;
    private String lokacijaSlike = "";
    private int brojPrijava;
    private int brojPrekrsaja;

    public ReceptBuilder setNaslovRecepta(String naslovRecepta) {
        this.naslovRecepta = naslovRecepta;
        return this;
    }

    public ReceptBuilder setImeAutora(String imeAutora) {
        this.imeAutora = imeAutora;
        return this;
    }


    public ReceptBuilder setSlikaRecepta(String slikaRecepta) {
        this.slikaRecepta = slikaRecepta;
        return this;
    }

    public ReceptBuilder setPrivatnaObjava(boolean privatnaObjava) {
        this.privatnaObjava = privatnaObjava;
        return this;
    }

    public ReceptBuilder setKoraci(List<String> koraci) {
        this.koraci = koraci;
        return this;
    }

    public ReceptBuilder setSastojci(List<String> sastojci) {
        this.sastojci = sastojci;
        return this;
    }

    public ReceptBuilder setVrijemePripreme(int vrijemePripreme) {
        this.vrijemePripreme = vrijemePripreme;
        return this;
    }

    public ReceptBuilder setTezinaPripreme(int tezinaPripreme) {
        this.tezinaPripreme = tezinaPripreme;
        return this;
    }

    public ReceptBuilder setBrojOsoba(int brojOsoba) {
        this.brojOsoba = brojOsoba;
        return this;
    }

    public ReceptBuilder setBrojSvidjanja(long brojSvidjanja) {
        this.brojSvidjanja = brojSvidjanja;
        return this;
    }

    public ReceptBuilder setKategorijaRecepta(String kategorijaRecepta) {
        this.kategorijaRecepta = kategorijaRecepta;
        return this;
    }

    public ReceptBuilder setAutor(String autor) {
        this.autor = autor;
        return this;
    }

    public ReceptBuilder setDatum(Timestamp datum) {
        this.datum = datum;
        return this;
    }

    public ReceptBuilder setLokacijaSlike(String lokacijaSlike) {
        this.lokacijaSlike = lokacijaSlike;
        return this;
    }

    public ReceptBuilder setBrojPrijava(int brojPrijava) {
        this.brojPrijava = brojPrijava;
        return this;
    }

    public ReceptBuilder setBrojPrekrsaja(int brojPrekrsaja) {
        this.brojPrekrsaja = brojPrekrsaja;
        return this;
    }

    public Map<String, Object> createHashMap() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("naslovRecepta", naslovRecepta);
        map.put("imeAutora", imeAutora);
        map.put("slikaRecepta", slikaRecepta);
        map.put("privatnaObjava", privatnaObjava);
        map.put("koraci", koraci);
        map.put("sastojci", sastojci);
        map.put("vrijemePripreme", vrijemePripreme);
        map.put("tezinaPripreme", tezinaPripreme);
        map.put("brojOsoba", brojOsoba);
        map.put("brojSvidjanja", brojSvidjanja);
        map.put("kategorijaRecepta", kategorijaRecepta);
        map.put("datum", datum);
        map.put("lokacijaSlike", lokacijaSlike);
        map.put("brojPrijava", brojPrijava);
        map.put("brojPrekrsaja", brojPrekrsaja);
        return map;
    }


    public Recept createRecept() {
        return new Recept(naslovRecepta, imeAutora, slikaRecepta, kategorijaRecepta, lokacijaSlike,
                datum, koraci, sastojci, vrijemePripreme, tezinaPripreme, brojOsoba, brojPrijava, brojPrekrsaja,
                brojSvidjanja, privatnaObjava);
    }
}