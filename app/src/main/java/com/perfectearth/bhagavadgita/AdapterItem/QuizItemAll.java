package com.perfectearth.bhagavadgita.AdapterItem;

public class QuizItemAll {

    public QuizItemAll() {
    }

    String score , name;

    public QuizItemAll(String score, String name) {
        this.score = score;
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
