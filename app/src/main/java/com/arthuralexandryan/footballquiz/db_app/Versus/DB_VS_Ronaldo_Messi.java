package com.arthuralexandryan.footballquiz.db_app.Versus;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DB_VS_Ronaldo_Messi extends RealmObject {
    @PrimaryKey
    private int id;
    private String question;
    private String answer_A;
    private String answer_B;
    private String answer_C;
    private String answer_D;
    private String right_answer;
    private boolean isAnswered;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer_A() { return answer_A; }
    public void setAnswer_A(String answer_A) { this.answer_A = answer_A; }

    public String getAnswer_B() { return answer_B; }
    public void setAnswer_B(String answer_B) { this.answer_B = answer_B; }

    public String getAnswer_C() { return answer_C; }
    public void setAnswer_C(String answer_C) { this.answer_C = answer_C; }

    public String getAnswer_D() { return answer_D; }
    public void setAnswer_D(String answer_D) { this.answer_D = answer_D; }

    public String getRight_answer() { return right_answer; }
    public void setRight_answer(String right_answer) { this.right_answer = right_answer; }

    public boolean isAnswered() { return isAnswered; }
    public void setAnswered(boolean answered) { isAnswered = answered; }
}
