package com.perfectearth.bhagavadgita.Fragment;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.perfectearth.bhagavadgita.Adapter.BookmarksAdapter;
import com.perfectearth.bhagavadgita.AdapterItem.BookMarkItem;
import com.perfectearth.bhagavadgita.R;
import com.perfectearth.bhagavadgita.VerseActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class QuotesFragment extends Fragment implements BookmarksAdapter.ItemBookmarkClickListener, BookmarksAdapter.OnItemLongClickListener {

    private RecyclerView quRecyclerView;
    private BookmarksAdapter quAdapter;
    private ArrayList<BookMarkItem> quotesList;

    public QuotesFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View quotesView = inflater.inflate(R.layout.fragment_quotes, container, false);
        quRecyclerView = quotesView.findViewById(R.id.recycler_quotes);
        quRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        quotesList = new ArrayList<>();
        quAdapter = new BookmarksAdapter(quotesList, getContext(), this,this);
        quRecyclerView.setAdapter(quAdapter);
        LoadAllName();
        return quotesView;
    }

    private void LoadAllName() {
        quotesList.clear();
        try {
            AssetManager assetManager = getContext().getAssets();
            InputStream inputStream = assetManager.open("19.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONArray jsonArrayAll = new JSONArray(stringBuilder.toString());
            for (int i = 0; i < jsonArrayAll.length(); i++) {
                quotesList.add(new BookMarkItem(jsonArrayAll.getJSONObject(i).
                        getString("verseB"), jsonArrayAll.getJSONObject(i).
                        getString("name"),jsonArrayAll.getJSONObject(i).
                        getString("serial")));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onItemLongClick(int position) {

    }

    @Override
    public void onBookmarkClick(int position, int verseCode) {
        String chapterSerial = String.valueOf(position);
        String verseSerial = String.valueOf(verseCode);
        Intent intent = new Intent(getContext(), VerseActivity.class);
        intent.putExtra("chapterSerial",chapterSerial);
        intent.putExtra("verseSerial",verseSerial);
        startActivity(intent);

    }
}