package com.arthuralexandryan.footballquiz.db_app.Score;

import io.realm.RealmObject;

public class Scores extends RealmObject {
    private int all;
    private Top5_scores top5_scores;
    private UFA_scores ufa_scores;
    private WorldChampionship_scores world_scores;
    private Versus_scores versus_scores;

    public int getAll() {
        return all;
    }

    public void setAll(int all) {
        this.all = all;
    }

    public Top5_scores getTop5_scores() {
        return top5_scores;
    }

    public void setTop5_scores(Top5_scores top5_scores) {
        this.top5_scores = top5_scores;
    }

    public UFA_scores getUfa_scores() {
        return ufa_scores;
    }

    public void setUfa_scores(UFA_scores ufa_scores) {
        this.ufa_scores = ufa_scores;
    }

    public WorldChampionship_scores getWorld_scores() {
        return world_scores;
    }

    public void setWorld_scores(WorldChampionship_scores world_scores) {
        this.world_scores = world_scores;
    }

    public Versus_scores getVersus_scores() {
        return versus_scores;
    }

    public void setVersus_scores(Versus_scores versus_scores) {
        this.versus_scores = versus_scores;
    }

    @Override
    public String toString() {
        return "Scores{" +
                "all=" + all +
                ", top5_scores=" + top5_scores +
                ", ufa_scores=" + ufa_scores +
                ", world_scores=" + world_scores +
                ", versus_scores=" + versus_scores +
                '}';
    }
}
