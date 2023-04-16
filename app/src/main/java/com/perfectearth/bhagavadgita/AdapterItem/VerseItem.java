package com.perfectearth.bhagavadgita.AdapterItem;

public class VerseItem {
    private String verseS;
    private String verseBS;
    private String verseB;

    public VerseItem(String verseS, String verseBS, String verseB) {
        this.verseS = verseS;
        this.verseBS = verseBS;
        this.verseB = verseB;
    }

    public String getVerseS() {
        return verseS;
    }

    public String getVerseBS() {
        return verseBS;
    }

    public String getVerseB() {
        return verseB;
    }
}