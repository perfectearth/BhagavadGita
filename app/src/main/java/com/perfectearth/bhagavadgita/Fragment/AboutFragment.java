package com.perfectearth.bhagavadgita.Fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.perfectearth.bhagavadgita.R;
import com.perfectearth.bhagavadgita.Utilis.ColoredHtmlTagHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AboutFragment extends Fragment {

    public AboutFragment() {

    }
    private TextView aboutShow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View aboutView =  inflater.inflate(R.layout.fragment_about, container, false);

        aboutShow = aboutView.findViewById(R.id.text_about);
        try {
            InputStream stream = getContext().getAssets().open("bappy.html");
            String htmlString = streamToString(stream);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
               aboutShow.setText(Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY, null, new ColoredHtmlTagHandler()));
            } else {
                aboutShow.setText(Html.fromHtml(htmlString, null, new ColoredHtmlTagHandler()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return aboutView;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

    }
    private String streamToString(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }
}