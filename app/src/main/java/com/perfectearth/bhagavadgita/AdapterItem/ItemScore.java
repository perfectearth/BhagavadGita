package com.perfectearth.bhagavadgita.AdapterItem;

public class ItemScore {
    String ScoreTotal ,ScoreWrong,ScoreChapter,ScoreCorrect;

    public ItemScore() {
    }

    public ItemScore(String scoreTotal, String scoreWrong, String scoreChapter, String scoreCorrect) {
        ScoreTotal = scoreTotal;
        ScoreWrong = scoreWrong;
        ScoreChapter = scoreChapter;
        ScoreCorrect = scoreCorrect;
    }

    public String getScoreTotal() {
        return ScoreTotal;
    }

    public void setScoreTotal(String scoreTotal) {
        ScoreTotal = scoreTotal;
    }

    public String getScoreWrong() {
        return ScoreWrong;
    }

    public void setScoreWrong(String scoreWrong) {
        ScoreWrong = scoreWrong;
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
