package com.perfectearth.bhagavadgita.AdapterItem;

public class BookMarkItem {
    String bookDetails;
    String bookSerial;
    String lastGita;

    public BookMarkItem(String bookDetails, String bookSerial, String lastGita) {
        this.bookDetails = bookDetails;
        this.bookSerial = bookSerial;
        this.lastGita = lastGita;
    }

    public String getBookDetails() {
        return bookDetails;
    }

    public String getBookSerial() {
        return bookSerial;
    }

    public String getLastGita() {
        return lastGita;
    }
}
