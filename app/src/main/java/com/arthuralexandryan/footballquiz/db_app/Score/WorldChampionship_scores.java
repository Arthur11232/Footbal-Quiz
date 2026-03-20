package com.arthuralexandryan.footballquiz.db_app.Score;

import io.realm.RealmObject;

public class WorldChampionship_scores extends RealmObject {
    private int all_world;
    private int all_answered_world;

    public int getAll_world() {
        return all_world;
    }

    public void setAll_world(int all_world) {
        this.all_world = all_world;
    }

    public int getAll_answered_world() {
        return all_answered_world;
    }

    public void setAll_answered_world(int all_answered_world) {
        this.all_answered_world = all_answered_world;
    }

    @Override
    public String toString() {
        return "WorldChampionship_scores{" +
                "all_world=" + all_world +
                ", all_answered_world=" + all_answered_world +
                '}';
    }
}
