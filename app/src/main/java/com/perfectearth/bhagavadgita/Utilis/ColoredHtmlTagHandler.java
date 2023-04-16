package com.perfectearth.bhagavadgita.Utilis;

import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;

public class ColoredHtmlTagHandler implements Html.TagHandler {

    private int startIndex = 0;
    private int lastIndex = 0;
    private String color = null;

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (tag.equalsIgnoreCase("font")) {
            if (opening) {
                startIndex = output.length();
                String color = getAttribute((XmlPullParser) xmlReader, "color");
                if (color != null) {
                    this.color = color;
                }
            } else {
                lastIndex = output.length();
                if (color != null) {
                    output.setSpan(new ForegroundColorSpan(Color.parseColor(color)), startIndex, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    color = null;
                }
            }
        }

    }

    private String getAttribute(XmlPullParser parser, String attributeName) {
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            if (parser.getAttributeName(i).equalsIgnoreCase(attributeName)) {
                return parser.getAttributeValue(i);
            }
        }
        return null;
    }
}
