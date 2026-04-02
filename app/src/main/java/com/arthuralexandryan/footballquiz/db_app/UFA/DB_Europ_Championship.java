package com.arthuralexandryan.footballquiz.db_app.UFA;

import com.arthuralexandryan.footballquiz.db_app.DB_Base;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DB_Europ_Championship extends RealmObject implements DB_Base {
    @PrimaryKey
    private int id;
    private String question;
    private String answer_A;
    private String answer_B;
    private String answer_C;
    private String answer_D;
    private String right_answer;
    private boolean isAnswered;

    @Override
    public int getId() { return id; }
    @Override
    public void setId(int id) { this.id = id; }

    @Override
    public String getQuestion() { return question; }
    @Override
    public void setQuestion(String question) { this.question = question; }

    @Override
    public String getAnswer_A() { return answer_A; }
    @Override
    public void setAnswer_A(String answer_A) { this.answer_A = answer_A; }

    @Override
    public String getAnswer_B() { return answer_B; }
    @Override
    public void setAnswer_B(String answer_B) { this.answer_B = answer_B; }

    @Override
    public String getAnswer_C() { return answer_C; }
    @Override
    public void setAnswer_C(String answer_C) { this.answer_C = answer_C; }

    @Override
    public String getAnswer_D() { return answer_D; }
    @Override
    public void setAnswer_D(String answer_D) { this.answer_D = answer_D; }

    @Override
    public String getRight_answer() { return right_answer; }
    @Override
    public void setRight_answer(String right_answer) { this.right_answer = right_answer; }

    @Override
    public boolean isAnswered() { return isAnswered; }
    @Override
    public void setAnswered(boolean answered) { isAnswered = answered; }
}
