package com.perfectearth.bhagavadgita.AdapterItem;

public class Question {

    private String AnsA,AnsB,AnsC,AnsD,ChapterId,CorrectAnswer,Question;

    public Question() {
    }
    public Question(String ansA, String ansB, String ansC, String ansD, String chapterId, String correctAnswer, String question) {
        AnsA = ansA;
        AnsB = ansB;
        AnsC = ansC;
        AnsD = ansD;
        ChapterId = chapterId;
        CorrectAnswer = correctAnswer;
        Question = question;
    }

    public String getAnsA() {
        return AnsA;
    }

    public void setAnsA(String ansA) {
        AnsA = ansA;
    }

    public String getAnsB() {
        return AnsB;
    }

    public void setAnsB(String ansB) {
        AnsB = ansB;
    }

    public String getAnsC() {
        return AnsC;
    }

    public void setAnsC(String ansC) {
        AnsC = ansC;
    }

    public String getAnsD() {
        return AnsD;
    }

    public void setAnsD(String ansD) {
        AnsD = ansD;
    }

    public String getChapterId() {
        return ChapterId;
    }

    public void setChapterId(String chapterId) {
        ChapterId = chapterId;
    }

    public String getCorrectAnswer() {
        return CorrectAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        CorrectAnswer = correctAnswer;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

}
