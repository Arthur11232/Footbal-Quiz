package com.arthuralexandryan.footballquiz.db_app.Score;

import com.arthuralexandryan.footballquiz.db_app.Score.Objects.PlacesTop5Obj;
import com.arthuralexandryan.footballquiz.db_app.Score.Objects.PlacesUFAObj;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FQ_Scores extends RealmObject {
    @PrimaryKey
    public int id;
    public int fq_all;
    public int top5;
    public int top5_answered;
    public int ufa;
    public int ufa_answered;
    public int world;
    public int world_answered;
    public int vsRM;
    public int vsRM_answered;
    public int vsRB;
    public int vsRB_answered;
    public PlacesTop5Obj top5Scores;
    public PlacesUFAObj ufaScores;
}
