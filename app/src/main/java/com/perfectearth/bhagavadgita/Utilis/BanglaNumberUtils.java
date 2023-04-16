package com.perfectearth.bhagavadgita.Utilis;

public class BanglaNumberUtils {
    private static final String[] BANGLA_NUMERALS = {"০", "১", "২", "৩", "৪", "৫", "৬", "৭", "৮", "৯"};

    public static String toBanglaNumber(int number) {
        String numberString = String.valueOf(number);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < numberString.length(); i++) {
            char c = numberString.charAt(i);
            if (Character.isDigit(c)) {
                int digit = Character.getNumericValue(c);
                stringBuilder.append(BANGLA_NUMERALS[digit]);
            } else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }
}
