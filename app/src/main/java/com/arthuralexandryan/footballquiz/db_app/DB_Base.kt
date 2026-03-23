package com.arthuralexandryan.footballquiz.db_app;

public interface DB_Base {
    int getId();
    void setId(int id);
    String getQuestion();
    void setQuestion(String question);
    String getAnswer_A();
    void setAnswer_A(String answer_A);
    String getAnswer_B();
    void setAnswer_B(String answer_B);
    String getAnswer_C();
    void setAnswer_C(String answer_C);
    String getAnswer_D();
    void setAnswer_D(String answer_D);
    String getRight_answer();
    void setRight_answer(String right_answer);
    boolean isAnswered();
    void setAnswered(boolean answered);
}
