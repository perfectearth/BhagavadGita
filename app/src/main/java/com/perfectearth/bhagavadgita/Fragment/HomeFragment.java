package com.perfectearth.bhagavadgita.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.perfectearth.bhagavadgita.Adapter.ChapterAdapter;
import com.perfectearth.bhagavadgita.AdapterItem.ChapterItem;
import com.perfectearth.bhagavadgita.DetailActivity;
import com.perfectearth.bhagavadgita.R;
import com.perfectearth.bhagavadgita.Utilis.BanglaNumberUtils;
import com.perfectearth.bhagavadgita.Utilis.ChapterAll;
import com.perfectearth.bhagavadgita.Utilis.CustomProgress;
import com.perfectearth.bhagavadgita.VerseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements ChapterAdapter.OnItemClickListener {

    public HomeFragment() {
        // Required empty public constructor
    }
    private NestedScrollView scrollView;
    private ChapterAdapter chapterAdapter;
    private CardView lastOpen;
    private ArrayList<ChapterItem> chapterList;

    private RecyclerView chapterView;

    private SharedPreferences prefsHome;

    private TextView lastDetail , lastSerial, viewChapter, mongolaDetials, btnDialog;
    private LinearLayout viewLastLiner;
    private String lastVerse,lastChapter;
    private static final String TAG = "HomeFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View homeView =  inflater.inflate(R.layout.fragment_home, container, false);

        scrollView = homeView.findViewById(R.id.scroll_view);

        scrollView.post(() ->scrollView.scrollTo(0, 0));

        CustomProgress.showProgressBar(getContext(), false,"Loading...");

        prefsHome = getActivity().getSharedPreferences("MyPrefsV", MODE_PRIVATE);
        lastDetail = homeView.findViewById(R.id.lp_verse_details);
        lastSerial = homeView.findViewById(R.id.lp_verse_serial);
        mongolaDetials = homeView.findViewById(R.id.mongola_data);
        viewLastLiner = homeView.findViewById(R.id.last_lin);
        viewChapter = homeView.findViewById(R.id.last_page_name);
        btnDialog = homeView.findViewById(R.id.read_btn);
        chapterView = homeView.findViewById(R.id.recycler_chapter);
        lastOpen = homeView.findViewById(R.id.last_card_click);
        int columns = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                && getResources().getConfiguration().screenWidthDp < 600 ? 2 : 4;
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), columns);
        chapterView.setLayoutManager(layoutManager);
        chapterView.setHasFixedSize(true);
        chapterView.setNestedScrollingEnabled(false);
        chapterList = new ArrayList<>();
        chreatData();
        String text = getString(R.string.mongola);
        String[] lines = text.split("a");
        StringBuilder modifiedText = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            modifiedText.append(lines[i]).append("🌸 ");
        }
        mongolaDetials.setText(modifiedText.toString().trim());


        lastOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLastRead(lastChapter,lastVerse);
            }
        });

        btnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        return homeView;

    }

    private void startLastRead(String lastChapter, String lastVerse) {
        String chapterSerial = String.valueOf(lastChapter);
        String verseSerial = String.valueOf(lastVerse);
        Intent intent = new Intent(getContext(), VerseActivity.class);
        intent.putExtra("chapterSerial",chapterSerial);
        intent.putExtra("verseSerial",verseSerial);
        startActivity(intent);
    }

    private void showDialog() {
        String text = getString(R.string.mongola);
        Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_read);
        TextView showText = dialog.findViewById(R.id.read_text_view);
        Button submitButton = dialog.findViewById(R.id.submit_button);


        String[] lines = text.split("a");
        StringBuilder modifiedText = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            modifiedText.append(lines[i]).append("🌸 ");
        }
        showText.setText(modifiedText.toString().trim());

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }



    private void chreatData() {
        try {
            AssetManager assetManager = getContext().getAssets();
            InputStream inputStream = assetManager.open("chapter.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String count = jsonObject.getString("count");
                String countText = count+" টি শ্লোক।";
                ChapterItem item = new ChapterItem(name,countText);
                chapterList.add(item);
            }
            chapterAdapter = new ChapterAdapter(chapterList,this);
            chapterView.setAdapter(chapterAdapter);
            stopDialog();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            stopDialog();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        checkLast();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void checkLast(){
        lastChapter = prefsHome.getString("chapterLast", "serial");
        lastVerse = prefsHome.getString("verseLast", "serial");
        String lastDetails = prefsHome.getString("verseBLast", "serial");
        if (TextUtils.isDigitsOnly(lastChapter)) {
            showLast(lastChapter,lastVerse,lastDetails);
            if (viewLastLiner.getVisibility()==View.VISIBLE){
            }else{
                viewLastLiner.setVisibility(View.VISIBLE);
            }
        } else {
            Log.d(TAG, "Not ok" );
        }
    }

    private void showLast(String lastChapter, String lastVerse, String lastDetails) {
        ChapterAll chapters = new ChapterAll();
        List<String> chapterNames = chapters.getChapterNames();
        int chapterNumber = Integer.parseInt(lastChapter);
        if (chapterNumber >= 1 && chapterNumber <= chapterNames.size()) {
            String chapterName = chapterNames.get(chapterNumber - 1);
            viewChapter.setText(chapterName);
        } else {
            viewChapter.setText("No matching chapter name found");
        }
        String banglaNumber = BanglaNumberUtils.toBanglaNumber(Integer.parseInt(lastVerse));
        lastDetail.setText(lastDetails);
        lastSerial.setText("শ্লোক :"+banglaNumber);
    }

    @Override
    public void onItemClick(String position) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra("position",position);
        startActivity(intent);

    }
    private void stopDialog(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CustomProgress.hideProgressBar();
            }
        }, 700);
    }
}