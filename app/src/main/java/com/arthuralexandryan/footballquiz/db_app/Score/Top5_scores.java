package com.arthuralexandryan.footballquiz.db_app.Score;

import com.arthuralexandryan.footballquiz.db_app.Score.Objects.PlacesTop5Obj;
import io.realm.RealmObject;

public class Top5_scores extends RealmObject {
    private int all_top5;
    private int all_answered_top5;
    private PlacesTop5Obj top5Obj;

    public int getAll_top5() {
        return all_top5;
    }

    public void setAll_top5(int all_top5) {
        this.all_top5 = all_top5;
    }

    public int getAll_answered_top5() {
        return all_answered_top5;
    }

    public void setAll_answered_top5(int all_answered_top5) {
        this.all_answered_top5 = all_answered_top5;
    }

    public PlacesTop5Obj getTop5Obj() {
        return top5Obj;
    }

    public void setTop5Obj(PlacesTop5Obj top5Obj) {
        this.top5Obj = top5Obj;
    }

    @Override
    public String toString() {
        return "Top5_scores{" +
                "all_top5=" + all_top5 +
                ", all_answered_top5=" + all_answered_top5 +
                ", top5Obj=" + top5Obj +
                '}';
    }
}
