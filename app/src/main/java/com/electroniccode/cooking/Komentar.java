package com.electroniccode.cooking;

import com.google.firebase.firestore.PropertyName;

public class Komentar {

    private String komentar;
    private String autor;

    public Komentar() {}

    public Komentar(String komentar, String autor) {
        this.komentar = komentar;
        this.autor = autor;
    }

    @PropertyName("komentar")
    public String getKomentar() {
        return komentar;
    }

    @PropertyName("komentar")
    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    @PropertyName("imeAutora")
    public String getAutor() {
        return autor;
    }

    @PropertyName("imeAutora")
    public void setAutor(String autor) {
        this.autor = autor;
    }
}
