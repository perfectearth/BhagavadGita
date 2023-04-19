package com.perfectearth.bhagavadgita.AdapterItem;

public class ItemScore {
    String quesTotal, ScoreChapter, ScoreCorrect;

    public ItemScore() {
    }

    public ItemScore(String quesTotal, String scoreChapter, String scoreCorrect) {
        this.quesTotal = quesTotal;
        ScoreChapter = scoreChapter;
        ScoreCorrect = scoreCorrect;
    }

    public String getQuesTotal() {
        return quesTotal;
    }

    public void setQuesTotal(String quesTotal) {
        this.quesTotal = quesTotal;
    }

    public String getScoreChapter() {
        return ScoreChapter;
    }

    public void setScoreChapter(String scoreChapter) {
        ScoreChapter = scoreChapter;
    }

    public String getScoreCorrect() {
        return ScoreCorrect;
    }

    public void setScoreCorrect(String scoreCorrect) {
        ScoreCorrect = scoreCorrect;
    }
}