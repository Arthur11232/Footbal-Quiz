package com.arthuralexandryan.footballquiz.db_app.Score;

import com.arthuralexandryan.footballquiz.db_app.Score.Objects.PlacesUFAObj;
import io.realm.RealmObject;

public class UFA_scores extends RealmObject {
    private int all_ufa;
    private int all_answered_ufa;
    private PlacesUFAObj ufaObj;

    public int getAll_ufa() {
        return all_ufa;
    }

    public void setAll_ufa(int all_ufa) {
        this.all_ufa = all_ufa;
    }

    public int getAll_answered_ufa() {
        return all_answered_ufa;
    }

    public void setAll_answered_ufa(int all_answered_ufa) {
        this.all_answered_ufa = all_answered_ufa;
    }

    public PlacesUFAObj getUfaObj() {
        return ufaObj;
    }

    public void setUfaObj(PlacesUFAObj ufaObj) {
        this.ufaObj = ufaObj;
    }

    @Override
    public String toString() {
        return "UFA_scores{" +
                "all_ufa=" + all_ufa +
                ", all_answered_ufa=" + all_answered_ufa +
                ", ufaObj=" + ufaObj +
                '}';
    }
}
