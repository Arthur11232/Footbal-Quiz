package com.arthuralexandryan.footballquiz.db_app.Score.Objects;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PlacesUFAObj extends RealmObject {
    @PrimaryKey
    public int id;
    public int superCup_score;
    public int superCup_answered;
    public int europaLeague_score;
    public int europaLeague_answered;
    public int europaChampionship_score;
    public int europaChampionship_answered;
    public int championsLeague_score;
    public int championsLeague_answered;
}
