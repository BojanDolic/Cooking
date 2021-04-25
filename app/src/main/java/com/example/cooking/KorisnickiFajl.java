package com.example.cooking;

import java.util.List;

public class KorisnickiFajl {

    private List<String> lajkovaniRecepti;


    public KorisnickiFajl() {
    }

    public KorisnickiFajl(List<String> lajkovaniRecepti) {
        this.lajkovaniRecepti = lajkovaniRecepti;
    }

    public List<String> getLajkovaniRecepti() {
        return lajkovaniRecepti;
    }

    public void setLajkovaniRecepti(List<String> lajkovaniRecepti) {
        this.lajkovaniRecepti = lajkovaniRecepti;
    }
}
