package com.perfectearth.bhagavadgita.Utilis;

import java.util.Arrays;
import java.util.List;

public class ChapterAll {
    private List<String> chapterNames;
    public ChapterAll() {
        chapterNames = Arrays.asList(
                "প্রথম অধ্যায়", "দ্বিতীয় অধ্যায়", "তৃতীয় অধ্যায়",
                "চতুর্থ অধ্যায়", "পঞ্চম অধ্যায়", "ষষ্ঠ অধ্যায়",
                "সপ্তম অধ্যায়", "অষ্টম অধ্যায়", "নবম অধ্যায়",
                "দশম অধ্যায়", "একাদশ অধ্যায়", "দ্বাদশ অধ্যায়",
                "ত্রয়োদশ অধ্যায়", "চতুর্দশ অধ্যায়", "পঞ্চদশ অধ্যায়",
                "ষোড়শ অধ্যায়", "সপ্তদশ অধ্যায়", "অষ্টাদশ অধ্যায়",
                "গীতা-মাহাত্ম্য"
        );
    }
    public List<String> getChapterNames() {
        return chapterNames;
    }

    public String getChapterName(int index) {
        return chapterNames.get(index);
    }
}