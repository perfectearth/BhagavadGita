package com.perfectearth.bhagavadgita.Utilis;

public class OrdinalUtilis {
    public static String getOrdinalSuffix(int num) {
        if (num % 100 == 11 || num % 100 == 12 || num % 100 == 13) {
            return num + "th";
        } else {
            switch (num % 10) {
                case 1:  return num + "st";
                case 2:  return num + "nd";
                case 3:  return num + "rd";
                default: return num + "th";
            }
        }
    }
}
