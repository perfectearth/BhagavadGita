package com.perfectearth.bhagavadgita;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.perfectearth.bhagavadgita.Adapter.DetailsAdapter;
import com.perfectearth.bhagavadgita.AdapterItem.VerseItem;
import com.perfectearth.bhagavadgita.Utilis.BanglaNumberUtils;
import com.perfectearth.bhagavadgita.Utilis.ChapterAll;
import com.perfectearth.bhagavadgita.Utilis.CustomProgress;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.perfectearth.bhagavadgita.Utilis.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements DetailsAdapter.VerseClick, NetworkUtils.OnNetworkCheckListener{

    private RecyclerView vRecyclerView;
    private DetailsAdapter vAdapter;
    private ArrayList<VerseItem> verseList;
    private TextView chapterName , chapterSerial, chapterDetails;
    String  positionString;
    private FloatingActionButton fabPre;
    private FloatingActionButton fabNext;
    private  JSONObject jsonElement;
    private JSONArray jsonArrayName;

    private SharedPreferences prefs;
    private float textSize , textSpacing;
    private NestedScrollView detailsScroll;
    private Typeface typeface;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        vRecyclerView = findViewById(R.id.recycler_view);
        vRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        verseList = new ArrayList<>();

        detailsScroll = findViewById(R.id.detailsScroll);

        detailsScroll.post(() ->detailsScroll.scrollTo(0, 0));

        CustomProgress.showProgressBar(this,false,"Loading...");

        vRecyclerView.setNestedScrollingEnabled(false);

        ImageButton clickFont = findViewById(R.id.click_font);

        if (mInterstitialAd==null){
            NetworkUtils.isNetworkAvailable(this, this);
        }

        prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        textSize = prefs.getFloat("textSize", 14f);
        textSpacing = prefs.getFloat("textSpacing", 1f);

        String typefaceName = prefs.getString("Typeface", "fonts/siyam_rupali.ttf");
        typeface = Typeface.createFromAsset(getAssets(), typefaceName);

        chapterName = findViewById(R.id.chapter_show);
        chapterSerial = findViewById(R.id.chapter_no);
        chapterDetails = findViewById(R.id.chapter_details);
        chapterDetails.setTextSize(textSize);
        chapterDetails.setLineSpacing(0f, textSpacing);

        fabPre = findViewById(R.id.prev_button);
        fabNext = findViewById(R.id.next_button);

        Intent intent = getIntent();
        positionString = intent.getStringExtra("position");

        chapterDetails.setTypeface(typeface);


        LoadName();
        LoadFile(positionString);
        LoadNameFull(positionString);
        ShowHideBTPrevise();
        ShowHideBTNext();


        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.font_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        ImageButton plusFont = bottomSheetDialog.findViewById(R.id.plus_font);
        ImageButton minusFont = bottomSheetDialog.findViewById(R.id.minus_font);
        Spinner spinner = bottomSheetDialog.findViewById(R.id.font_spinner);
        RadioGroup colorRadioGroup = bottomSheetDialog.findViewById(R.id.color_group);
        RelativeLayout rootLayout = findViewById(R.id.root_bg_details);

        String hexColor = prefs.getString("background_color", "#00000000");
        int iddd = prefs.getInt("ID_RADIO", 0);
        int backgroundColor;
        try {
            backgroundColor = Color.parseColor("#" + hexColor);
        } catch (IllegalArgumentException e) {
            backgroundColor = Color.TRANSPARENT;
        }
        if (iddd == 0) {
            rootLayout.setBackgroundColor(Color.TRANSPARENT);
        } else {
            rootLayout.setBackgroundColor(backgroundColor);
            colorRadioGroup.check(iddd);
        }

        colorRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int colorResId = 0;
                switch (i) {
                    case R.id.btn1:
                        colorResId = R.color.transparent;
                        break;
                    case R.id.btn2:
                        colorResId = R.color.gray;
                        break;
                    case R.id.btn3:
                        colorResId = R.color.gray2;
                        break;
                    default:
                        break;
                }
                if (colorResId != 0) {
                    int color = getResources().getColor(colorResId);
                    String hexColor = Integer.toHexString(color);
                    rootLayout.setBackgroundColor(color);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("background_color", hexColor);
                    editor.putInt("ID_RADIO", i);
                    editor.apply();
                    radioGroup.check(i);
                }
            }
        });



        int selectedButtonId = prefs.getInt("BUTTON_ID_DETAILS", -1);
        MaterialButtonToggleGroup toggleGroup = bottomSheetDialog.findViewById(R.id.toggleButton);

        if (selectedButtonId != -1) {
            toggleGroup.check(selectedButtonId);
        }else {
            toggleGroup.uncheck(selectedButtonId);
        }

        MaterialButton button1 = bottomSheetDialog.findViewById(R.id.bM1);
        MaterialButton button2 = bottomSheetDialog.findViewById(R.id.bM2);
        MaterialButton button3 = bottomSheetDialog.findViewById(R.id.bM3);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialButton button = (MaterialButton) view;
                int id = button.getId();
                if (button == button1) {
                    textSpacing = 1f;
                    toggleGroup.check(R.id.bM1);
                    toggleGroup.uncheck(R.id.bM2);
                    toggleGroup.uncheck(R.id.bM3);
                    initTextSpaching(textSpacing, id);

                } else if (button == button2) {
                    textSpacing = 1.5f;
                    initTextSpaching(textSpacing, id);
                    toggleGroup.uncheck(R.id.bM1);
                    toggleGroup.check(R.id.bM2);
                    toggleGroup.uncheck(R.id.bM3);
                }else if (button == button3) {
                    textSpacing = 2f;
                    initTextSpaching(textSpacing, id);
                    toggleGroup.uncheck(R.id.bM1);
                    toggleGroup.uncheck(R.id.bM2);
                    toggleGroup.check(R.id.bM3);
                }
                else {
                    System.out.println("Element not found.");
                }

            }
        };
        for (int i = 0; i < toggleGroup.getChildCount(); i++) {
            View view = toggleGroup.getChildAt(i);
            if (view instanceof MaterialButton) {
                MaterialButton button = (MaterialButton) view;
                button.setOnClickListener(onClickListener);
            }
        }
        button1.setOnClickListener(onClickListener);
        button2.setOnClickListener(onClickListener);
        button3.setOnClickListener(onClickListener);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Default Font", "Kalpurush", "Chalantika", "BishalStyle", "Ben Sen"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(prefs.getInt("lastSelectedPosition", 0));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = prefs.edit();
                String font = "";
                switch (position) {
                    case 1:
                        font = "fonts/kalpurush.ttf";
                        break;
                    case 2:
                        font = "fonts/chalantika.ttf";
                        break;
                    case 3:
                        font = "fonts/bishal_style.ttf";
                        break;
                    case 4:
                        font = "fonts/ben_sen.ttf";
                        break;
                    default:
                        font = "fonts/siyam_rupali.ttf";
                        break;
                }
                Typeface typeface = Typeface.createFromAsset(getBaseContext().getAssets(), font);
                editor.putString("Typeface", font);
                editor.putInt("lastSelectedPosition", position);
                editor.apply();
                chapterDetails.setTypeface(typeface);
                vAdapter.setTypeface(typeface);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                plusFont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textSize += 2f;
                        initTextSize(textSize);
                    }
                });
                minusFont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textSize -= 2f;
                        initTextSize(textSize);
                    }
                });
            }
        });

        clickFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.show();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fabPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = Integer.parseInt(positionString);
                value--;
                String newString = String.valueOf(value);
                positionString = newString;
                LoadFile(newString);
                LoadNameFull(newString);
                ShowHideBTNext();
                ShowHideBTPrevise();
            }
        });
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = Integer.parseInt(positionString);
                value++;
                String newString = String.valueOf(value);
                positionString = newString;
                LoadFile(newString);
                LoadNameFull(newString);
                ShowHideBTPrevise();
                ShowHideBTNext();
            }
        });
    }


    private void ShowHideBTNext() {
        if (positionString.equals("18")){
            fabNext.setVisibility(View.GONE);
        }else {
            if (fabNext.getVisibility() == View.INVISIBLE || fabNext.getVisibility() == View.GONE) {
                fabNext.setVisibility(View.VISIBLE);
            }
        }
    }

    private void ShowHideBTPrevise() {
       if (positionString.equals("1")){
            fabPre.setVisibility(View.GONE);
        }else {
            if (fabPre.getVisibility() == View.INVISIBLE || fabPre.getVisibility() == View.GONE) {
                fabPre.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initTextSize(float view) {
        chapterDetails.setTextSize(view);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("textSize", view);
        editor.apply();
        vAdapter.setTextSize(view);

    }
    private void LoadName() {
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("chapter.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            jsonArrayName = new JSONArray(stringBuilder.toString());
                // Check if the name property of the current element matches the name we're looking for
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
    private void LoadFile(String nameString) {
        verseList.clear();
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open(nameString+".json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String verseS = jsonObject.getString("verseS");
                String verseSB = jsonObject.getString("verseBS");
                String verseB = jsonObject.getString("verseB");
                VerseItem item = new VerseItem(verseS,verseSB,verseB);
                verseList.add(item);
            }
            vAdapter = new DetailsAdapter(verseList, this, textSize,typeface);
            vRecyclerView.setAdapter(vAdapter);
            vAdapter.notifyDataSetChanged();
            stopDialog();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            stopDialog();

        }
    }
    private void LoadNameFull(String chapterD) {
        for (int i = 0; i < jsonArrayName.length(); i++) {
            try {
                jsonElement = jsonArrayName.getJSONObject(i);
                if (jsonElement.getString("no").equals(chapterD)) {
                    if (jsonElement != null) {
                        String serials = jsonElement.getString("no");
                        String names = jsonElement.getString("name");
                        String details = jsonElement.getString("details");
                        chapterName.setText(names);
                        ChapterAll chapters = new ChapterAll();
                        List<String> chapterNames = chapters.getChapterNames();

                        int chapterNumber = Integer.parseInt(serials);
                        String title="";
                        if (chapterNumber >= 1 && chapterNumber <= chapterNames.size()) {
                            String chapterName = chapterNames.get(chapterNumber - 1);
                            title = chapterName;
                        }
                        chapterSerial.setText(title);
                        chapterDetails.setText(details);
                    } else {
                        System.out.println("Element not found.");
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void interAds() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ca-app-pub-8889566517679501/9227619100", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(DetailActivity.this);
                        } else {
                            if (mInterstitialAd == null) {
                                interAds();
                            }
                        }
                        Log.i(TAG, "onAdLoaded");
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        mInterstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                    }
                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        mInterstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }
                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
    }
    @Override
    public void onItemClickV(String verseSerial) {
        Intent intent = new Intent(this, VerseActivity.class);
        intent.putExtra("chapterSerial",positionString);
        intent.putExtra("verseSerial",verseSerial);
        startActivity(intent);

    }

    private void initTextSpaching(float textSpacing , int id) {
        chapterDetails.setLineSpacing(0f,textSpacing);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("textSpacing", textSpacing);
        editor.putInt("BUTTON_ID_DETAILS", id);
        editor.apply();
    }
    private void stopDialog(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CustomProgress.hideProgressBar();
            }
        }, 700);
    }

    @Override
    public void onNetworkCheck(boolean isNetworkAvailable) {
        if (isNetworkAvailable) {
            interAds();
        }
    }
}