package com.arthuralexandryan.footballquiz.db_app.Score.Objects;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PlacesTop5Obj extends RealmObject {
    @PrimaryKey
    public int id;
    public int france_score;
    public int france_answered;
    public int germany_score;
    public int germany_answered;
    public int italy_score;
    public int italy_answered;
    public int england_score;
    public int england_answered;
    public int spain_score;
    public int spain_answered;
}
