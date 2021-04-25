package com.example.cooking;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

public class QueryUtil {

    public QueryUtil() {}


    public static Query makeQuery(CollectionReference ref, String kategorija) {
        return ref.whereEqualTo("privatnaObjava", false)
                .whereEqualTo("kategorijaRecepta", kategorija)
                .orderBy("datum", Query.Direction.DESCENDING);
    }

    public static Query makeQuery(CollectionReference ref, String kategorija, int vrijemePripreme) {

        return ref.whereEqualTo("privatnaObjava", false)
                .whereEqualTo("kategorijaRecepta", kategorija)
                .whereLessThanOrEqualTo("vrijemePripreme", vrijemePripreme)
                .orderBy("vrijemePripreme", Query.Direction.DESCENDING)
                .orderBy("datum", Query.Direction.DESCENDING);
    }


    public static Query makeQuery(CollectionReference ref, String kategorija, int vrijemePripreme, int tezinaPripreme) {
        return ref.whereEqualTo("privatnaObjava", false)
                .whereEqualTo("kategorijaRecepta", kategorija)
                .whereLessThanOrEqualTo("vrijemePripreme", vrijemePripreme)
                .whereEqualTo("tezinaPripreme", tezinaPripreme)
                .orderBy("vrijemePripreme", Query.Direction.DESCENDING)
                .orderBy("datum", Query.Direction.DESCENDING);
    }


    public static Query makeQuery(CollectionReference ref, int vrijemePripreme, int tezinaPripreme) {
        return ref.whereEqualTo("privatnaObjava", false)
                .whereLessThanOrEqualTo("vrijemePripreme", vrijemePripreme)
                .whereEqualTo("tezinaPripreme", tezinaPripreme)
                .orderBy("vrijemePripreme", Query.Direction.DESCENDING)
                .orderBy("datum", Query.Direction.DESCENDING);
    }


    public static Query makeQuery(CollectionReference ref, int vrijemePripreme) {
        return ref.whereEqualTo("privatnaObjava", false)
                .whereLessThanOrEqualTo("vrijemePripreme", vrijemePripreme)
                .orderBy("vrijemePripreme", Query.Direction.DESCENDING)
                .orderBy("datum", Query.Direction.DESCENDING);
    }

    public static Query makeQueryKategorijaTezina(CollectionReference ref, String kategorija, int tezinaPripreme) {
        return ref.whereEqualTo("privatnaObjava", false)
                .whereEqualTo("kategorijaRecepta", kategorija)
                .whereEqualTo("tezinaPripreme", tezinaPripreme)
                .orderBy("datum", Query.Direction.DESCENDING);
    }


    public static Query makeQueryTezina(CollectionReference ref, int tezinaPripreme) {
        return ref.whereEqualTo("privatnaObjava", false)
                .whereEqualTo("tezinaPripreme", tezinaPripreme)
                .orderBy("datum", Query.Direction.DESCENDING);
    }

}
