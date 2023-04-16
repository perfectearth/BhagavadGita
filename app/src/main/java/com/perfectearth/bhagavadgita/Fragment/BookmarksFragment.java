package com.perfectearth.bhagavadgita.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.perfectearth.bhagavadgita.Adapter.BookmarksAdapter;
import com.perfectearth.bhagavadgita.AdapterItem.BookMarkItem;
import com.perfectearth.bhagavadgita.Utilis.DBHelper;
import com.perfectearth.bhagavadgita.R;
import com.perfectearth.bhagavadgita.VerseActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class BookmarksFragment extends Fragment implements BookmarksAdapter.OnItemLongClickListener, BookmarksAdapter.ItemBookmarkClickListener{

    private DBHelper dbHelper;
    private RecyclerView bookRecyclerView;
    private BookmarksAdapter bookAdapter;
    private ArrayList<BookMarkItem> bookList;
    List<String> jsonObjectList;
    private TextView bookmarkText;
    private LinearLayout bookLayout;

    public BookmarksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View bookmarksView = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        dbHelper = new DBHelper(getContext());
        bookRecyclerView = bookmarksView.findViewById(R.id.recycler_bookmarks);
        bookLayout = bookmarksView.findViewById(R.id.lin_bookmarks);
        bookmarkText = bookmarksView.findViewById(R.id.bookmark_text);
        bookRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookList = new ArrayList<>();
        bookAdapter = new BookmarksAdapter(bookList, getContext(), this,this);
        bookRecyclerView.setAdapter(bookAdapter);
        return bookmarksView;
    }

    private void getALLD() {
        try {
            jsonObjectList = dbHelper.getAllData();
            JSONArray jsonArrayAll = new JSONArray(jsonObjectList.toString());
            bookList.clear();
            for (int i = 0; i < jsonArrayAll.length(); i++) {
                bookList.add(new BookMarkItem(jsonArrayAll.getJSONObject(i).
                        getString("verseB"), jsonArrayAll.getJSONObject(i).
                        getString("cNo"),jsonArrayAll.getJSONObject(i).
                        getString("vNo")));
            }
            if (bookList.isEmpty()) {
                bookLayout.setVisibility(View.VISIBLE);
                textAnim();
            } else {
                bookLayout.setVisibility(View.GONE);
            }

            bookAdapter.notifyDataSetChanged(); // Call notifyDataSetChanged() after the loop completes

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getALLD();
    }
    @Override
    public void onItemLongClick(int position) {
        // Handle long click event (e.g. show a dialog to confirm delete)
        showDeleteConfirmationDialog(position);
        if (bookList.isEmpty()) {
            bookLayout.setVisibility(View.VISIBLE);
            textAnim();
        } else {
            bookLayout.setVisibility(View.GONE);
        }
    }

    private void showDeleteConfirmationDialog(int position) {
        bookList.remove(position);
        bookAdapter.notifyItemRemoved(position);
        bookAdapter.notifyItemRangeChanged(position, bookList.size());
    }
    private void textAnim(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(1500);
        bookmarkText.startAnimation(alphaAnimation);
    }

    @Override
    public void onBookmarkClick(int chapter , int verse) {
        String chapterSerial = String.valueOf(chapter);
        String verseSerial = String.valueOf(verse);
        Intent intent = new Intent(getContext(), VerseActivity.class);
        intent.putExtra("chapterSerial",chapterSerial);
        intent.putExtra("verseSerial",verseSerial);
        startActivity(intent);
    }
}
