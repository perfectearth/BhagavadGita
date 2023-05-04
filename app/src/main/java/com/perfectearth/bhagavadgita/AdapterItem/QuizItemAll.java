package com.perfectearth.bhagavadgita.AdapterItem;

public class QuizItemAll {

    public QuizItemAll() {
    }

    String score ,total, name, serial;

    public QuizItemAll(String score, String total, String name, String serial) {
        this.score = score;
        this.total = total;
        this.name = name;
        this.serial = serial;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
}
