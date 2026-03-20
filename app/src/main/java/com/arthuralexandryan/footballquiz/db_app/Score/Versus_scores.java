package com.arthuralexandryan.footballquiz.db_app.Score;

import io.realm.RealmObject;

public class Versus_scores extends RealmObject {
    private int all_versus;
    private int all_answered_versus;

    public int getAll_versus() {
        return all_versus;
    }

    public void setAll_versus(int all_versus) {
        this.all_versus = all_versus;
    }

    public int getAll_answered_versus() {
        return all_answered_versus;
    }

    public void setAll_answered_versus(int all_answered_versus) {
        this.all_answered_versus = all_answered_versus;
    }

    @Override
    public String toString() {
        return "Versus_scores{" +
                "all_versus=" + all_versus +
                ", all_answered_versus=" + all_answered_versus +
                '}';
    }
}
