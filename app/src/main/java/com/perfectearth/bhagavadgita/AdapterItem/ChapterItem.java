package com.perfectearth.bhagavadgita.AdapterItem;

public class ChapterItem {
    public ChapterItem() {

    }
    private  String chapterName;
    private  String chapterCount;

    public ChapterItem(String chapterName, String chapterCount) {
        this.chapterName = chapterName;
        this.chapterCount = chapterCount;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(String chapterCount) {
        this.chapterCount = chapterCount;
    }
}
