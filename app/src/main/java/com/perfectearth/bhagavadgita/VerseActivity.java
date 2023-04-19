package com.perfectearth.bhagavadgita;

import android.Manifest;
import static android.content.ContentValues.TAG;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GestureDetectorCompat;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.perfectearth.bhagavadgita.Utilis.BanglaNumberUtils;
import com.perfectearth.bhagavadgita.Utilis.BannerDialog;
import com.perfectearth.bhagavadgita.Utilis.ChapterAll;
import com.perfectearth.bhagavadgita.Utilis.CustomNestedScrollView;
import com.perfectearth.bhagavadgita.Utilis.CustomProgress;
import com.perfectearth.bhagavadgita.Utilis.DBHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VerseActivity extends AppCompatActivity implements GestureDetector.OnGestureListener , TextToSpeech.OnInitListener{
    private TextView verseText, verseSText, verseBSText, verseBText,bengaliTitle;
    private String verseNo , chapterNo , titleVerse, modifiedChapterName, checkB, checkS;
    private FloatingActionButton btNext;
    private FloatingActionButton btPrevious;
    private JSONObject jsonElement;
    private JSONArray jsonArrayList;
    private int lastFile;
    private LinearLayout banglaPodohLin , songskritLin;
    private GestureDetectorCompat mGestureDetector;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private SharedPreferences prefsVerse;
    private float textSize, textSpacing;
    private CheckBox bookBox;
    private DBHelper dbHelper;
    private TextToSpeech textToSpeech;

    private List<String> jsonDataList;
    private ImageButton speakSbtn, speakSBbtn, speakBbtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verse);

        Toolbar toolbar = findViewById(R.id.toolbarVerse);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mGestureDetector = new GestureDetectorCompat(this, this);

        dbHelper = new DBHelper(this);

        btPrevious = findViewById(R.id.prev_verse);
        btNext = findViewById(R.id.next_verse);

        CustomProgress.showProgressBar(this, false,"Loading...");



        verseText = findViewById(R.id.solok);
        verseSText = findViewById(R.id.sonsKritDetails);

        verseBSText = findViewById(R.id.banglaPodeDetails);
        verseBText = findViewById(R.id.banglaDetails);
        banglaPodohLin = findViewById(R.id.lin_benglapodoh);
        songskritLin = findViewById(R.id.lin_songskrit);
        bengaliTitle = findViewById(R.id.banglaTitle);

        textToSpeech = new TextToSpeech(this, this);

        speakSbtn = findViewById(R.id.btns_sound);
        speakSBbtn = findViewById(R.id.btnsb_sound);
        speakBbtn = findViewById(R.id.btnb_sound);

        AnimationDrawable runningAnimation = new AnimationDrawable();
        runningAnimation.addFrame(ResourcesCompat.getDrawable(getResources(), R.drawable.volume_b, null), 150);
        runningAnimation.addFrame(ResourcesCompat.getDrawable(getResources(), R.drawable.volume_c, null), 150);
        runningAnimation.addFrame(ResourcesCompat.getDrawable(getResources(), R.drawable.volume_d, null), 150);
        runningAnimation.setOneShot(false);

        speakSbtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.volume_a, null));
        speakSBbtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.volume_a, null));
        speakBbtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.volume_a, null));

        View.OnClickListener clickListener = v -> {
            ImageButton speakBtn = null;
            TextView speakText = null;
            if (v == speakSbtn) {
                speakBtn = speakSbtn;
                speakText = verseSText;
            } else if (v == speakSBbtn) {
                speakBtn = speakSBbtn;
                speakText = verseBSText;
            } else if (v == speakBbtn) {
                speakBtn = speakBbtn;
                speakText = verseBText;
            }

            if (speakBtn != null && speakText != null) {
                if (textToSpeech.isSpeaking()) {
                    runningAnimation.stop();
                    runningAnimation.selectDrawable(0);
                    speakBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.volume_a, null));
                    textToSpeech.stop();
                } else {
                    speakBtn.setBackground(runningAnimation);
                    speakText(speakText.getText().toString());
                    runningAnimation.start();
                }
            }
        };

        speakSbtn.setOnClickListener(clickListener);
        speakSBbtn.setOnClickListener(clickListener);
        speakBbtn.setOnClickListener(clickListener);

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
            }

            @Override
            public void onDone(String utteranceId) {
                runningAnimation.stop();
                speakBbtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.volume_a, null));
                speakSbtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.volume_a, null));
                speakSBbtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.volume_a, null));
            }

            @Override
            public void onError(String utteranceId) {
                Log.e("MyActivity", "Error during speech synthesis");
            }
        });


        bookBox = findViewById(R.id.bookCheck);
        jsonDataList = dbHelper.getALLID();
        bookBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getJson();

            }
        });

        ImageView clickFont = findViewById(R.id.verse_font);

        CustomNestedScrollView customNestedScrollView = findViewById(R.id.customScroll);

        customNestedScrollView.post(() ->customNestedScrollView.scrollTo(0, 0));

        customNestedScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });


        prefsVerse = getSharedPreferences("MyPrefsV", MODE_PRIVATE);
        textSize = prefsVerse.getFloat("textSizeV", 14f);
        textSpacing = prefsVerse.getFloat("textSpacingV", 1f);

        checkS = prefsVerse.getString("checkVisibleS", "s");
        checkB = prefsVerse.getString("checkVisibleB", "s");

        String typefaceName = prefsVerse.getString("Typeface", "fonts/siyam_rupali.ttf");
        Typeface typeface = Typeface.createFromAsset(getAssets(), typefaceName);

        Intent intent = getIntent();
        chapterNo = intent.getStringExtra("chapterSerial");
        verseNo = intent.getStringExtra("verseSerial");


        ChapterAll chapters = new ChapterAll();
        List<String> chapterNames = chapters.getChapterNames();
        int chapterNumber = Integer.parseInt(chapterNo);
        if (chapterNumber >= 1 && chapterNumber <= chapterNames.size()) {
            modifiedChapterName = chapterNames.get(chapterNumber - 1);
            titleVerse = modifiedChapterName +"\n"+ " শ্লোক : ";
        } else {
            verseText.setText("No matching chapter name found");
        }


        verseSText.setTypeface(typeface);
        verseBSText.setTypeface(typeface);
        verseBText.setTypeface(typeface);

        verseSText.setTextSize(textSize);
        verseBSText.setTextSize(textSize);
        verseBText.setTextSize(textSize);

        verseSText.setLineSpacing(0f, textSpacing);
        verseBSText.setLineSpacing(0f, textSpacing);
        verseBText.setLineSpacing(0f, textSpacing);

        LoadList(chapterNo);
        LoadListFull(verseNo);

        ShowHideBTNext();
        ShowHideBTPrevise();

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.font_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        ImageButton plusFont = bottomSheetDialog.findViewById(R.id.plus_font);
        ImageButton minusFont = bottomSheetDialog.findViewById(R.id.minus_font);
        SwitchMaterial bSwitch = bottomSheetDialog.findViewById(R.id.switchBverse);
        SwitchMaterial sSwitch = bottomSheetDialog.findViewById(R.id.switchSverse);
        LinearLayout viewAllSwitch = bottomSheetDialog.findViewById(R.id.font_verse_show);
        Spinner spinner = bottomSheetDialog.findViewById(R.id.font_spinner);
        RadioGroup colorRadioGroup = bottomSheetDialog.findViewById(R.id.color_group);
        RelativeLayout rootLayout = findViewById(R.id.root_layout);

        viewAllSwitch.setVisibility(View.VISIBLE);

        String hexColor = prefsVerse.getString("background_color", "#00000000");
        int iddd = prefsVerse.getInt("ID_RADIO", 0);
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
                    SharedPreferences.Editor editor = prefsVerse.edit();
                    editor.putString("background_color", hexColor);
                    editor.putInt("ID_RADIO", i);
                    editor.apply();
                    radioGroup.check(i);
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Default Font", "Kalpurush", "Chalantika", "BishalStyle","Ben Sen"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(prefsVerse.getInt("lastSelectedPosition", 0));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = prefsVerse.edit();
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
                verseBSText.setTypeface(typeface);
                verseSText.setTypeface(typeface);
                verseBText.setTypeface(typeface);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        setViewVisibility(sSwitch, songskritLin,viewAllSwitch, checkS);
        setViewVisibility(bSwitch, banglaPodohLin,viewAllSwitch, checkB);

        int selectedButtonId = prefsVerse.getInt("BUTTON_ID_VERSE", -1);
        MaterialButtonToggleGroup toggleGroup = bottomSheetDialog.findViewById(R.id.toggleButton);

        if (selectedButtonId != -1) {
            toggleGroup.check(selectedButtonId);
        } else {
            Log.d("BTN m code", "no");
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
                } else if (button == button3) {
                    textSpacing = 2f;
                    initTextSpaching(textSpacing, id);
                    toggleGroup.uncheck(R.id.bM1);
                    toggleGroup.uncheck(R.id.bM2);
                    toggleGroup.check(R.id.bM3);
                } else {
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

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                // back button pressed
            }
        });

        btPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = Integer.parseInt(verseNo);
                value--;
                String newString = String.valueOf(value);
                verseNo = newString;
                LoadListFull(verseNo);
                ShowHideBTPrevise();
                ShowHideBTNext();
            }
        });

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = Integer.parseInt(verseNo);
                value++;
                String newString = String.valueOf(value);
                verseNo = newString;
                LoadListFull(verseNo);
                ShowHideBTPrevise();
                ShowHideBTNext();
            }
        });

        sSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                songskritLin.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                checkS = isChecked ? "Visible" : "Gone";
                prefsVerse.edit().putString("checkVisibleS", isChecked ? "Visible" : "Gone").apply();
            }
        });

        bSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                banglaPodohLin.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                checkB = isChecked ? "Visible" : "Gone";
                prefsVerse.edit().putString("checkVisibleB", isChecked ? "Visible" : "Gone").apply();
            }
        });
        clickFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.show();
                setViewVisibility(sSwitch, songskritLin, viewAllSwitch, checkS);
                setViewVisibility(bSwitch, banglaPodohLin, viewAllSwitch, checkB);

            }
        });

    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void setViewVisibility(SwitchMaterial switchVerse, LinearLayout viewVerse, LinearLayout viewAllSwitch, String checkSwitch) {
        if (bookBox.getVisibility() == View.VISIBLE){
            if (viewAllSwitch.getVisibility() == View.GONE){
                viewAllSwitch.setVisibility(View.VISIBLE);
            }
           if (checkSwitch.equals("Gone")) {
                switchVerse.setChecked(false);
                viewVerse.setVisibility(View.GONE);
            }else if (checkSwitch.equals("Visible")) {
               viewVerse.setVisibility(View.VISIBLE);
               switchVerse.setChecked(true);
           }
           else {
                viewVerse.setVisibility(View.VISIBLE);
                switchVerse.setChecked(true);
            }
        }else {
            if (chapterNo.equals("19")){
                if (checkSwitch.equals("Gone")) {
                    switchVerse.setChecked(false);
                    viewVerse.setVisibility(View.GONE);
                }else if (checkSwitch.equals("Visible")) {
                    viewVerse.setVisibility(View.VISIBLE);
                    switchVerse.setChecked(true);
                }
                else {
                    viewVerse.setVisibility(View.VISIBLE);
                    switchVerse.setChecked(true);
                }
            }else {
                viewAllSwitch.setVisibility(View.GONE);
            }
        }
    }

    private void checkBookButton() {
        try {
            boolean isFound = false;
            JSONArray jsonArrayAll = new JSONArray(jsonDataList.toString());
            for (int i = 0; i < jsonArrayAll.length(); i++) {
                JSONObject json = jsonArrayAll.getJSONObject(i);
                if (json.getString("id").equals(chapterNo + "n" + verseNo)) {
                    bookBox.setChecked(true);
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                bookBox.setChecked(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getJson(){
        String verseB = verseBText.getText().toString();
        String jsonData;

        List<JSONObject> jsonObjectList = new ArrayList<>();
        JSONObject json_data1 = new JSONObject();
        try {
            json_data1.put("id", chapterNo +"n"+verseNo);
            json_data1.put("cNo", chapterNo);
            json_data1.put("cNo", chapterNo);
            json_data1.put("vNo", verseNo);
            json_data1.put("verseB", verseB);
            jsonObjectList.add(json_data1);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        jsonData = json_data1.toString();
        if (bookBox.isChecked()){
            saveFavorutes(jsonData);
        }else {
            deleteFavorites();
        }
    }

    private void deleteFavorites() {
        boolean success = dbHelper.deleteDataByTextValue(chapterNo +"n"+verseNo);
        if (success) {
            Log.d(TAG, "JSON object with ID " + chapterNo + " has been deleted from the database.");
            jsonDataList = dbHelper.getALLID();
        } else {
            Log.d(TAG, "Failed to delete JSON object with ID " + chapterNo + " from the database.");
        }
        dbHelper.close();
    }

    private void saveFavorutes(String data) {
        boolean success = dbHelper.insertData(data);
        if (success){
            Toast.makeText(this, "Bookmark saved!", Toast.LENGTH_SHORT).show();
            jsonDataList = dbHelper.getALLID();
        }else {
            Toast.makeText(this, "not saved" , Toast.LENGTH_SHORT).show();
        }
        dbHelper.close();
    }

    private void dialog() {
        BannerDialog dialog = new BannerDialog();
        dialog.setTextD("Need permissions remember your favorite items. Please grant the necessary" +
                " permissions to ensure the app can save your preferences and provide you with a better experience.");
        Bundle args = new Bundle();
        args.putInt("icon_res_id", R.drawable.ic_alert);
        dialog.setArguments(args);
        dialog.setOnDialogButtonClickListener(new BannerDialog.OnDialogButtonClickListener() {
            @Override
            public void onYesButtonClick() {
                openAppSettings();
            }
            @Override
            public void onNoButtonClick() {
                dialog.dismiss();
            }
        });
        dialog.show(getSupportFragmentManager(), "banner_dialog");

    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkBookButton();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String getText = verseBText.getText().toString();
        if (bookBox.getVisibility() == View.VISIBLE){
            SharedPreferences.Editor editor = prefsVerse.edit();
            editor.putString("chapterLast",chapterNo);
            editor.putString("verseLast", verseNo);
            editor.putString("verseBLast", getText);
            editor.apply();
        }
    }

    private void initTextSpaching(float textSpacing, int id) {
        verseSText.setLineSpacing(0f,textSpacing);
        verseBSText.setLineSpacing(0f,textSpacing);
        verseBText.setLineSpacing(0f,textSpacing);
        SharedPreferences.Editor editor = prefsVerse.edit();
        editor.putFloat("textSpacingV", textSpacing);
        editor.putInt("BUTTON_ID_VERSE", id);
        editor.apply();
    }

    private void ShowHideBTPrevise() {
        if (verseNo.equals("1")){
            btPrevious.setVisibility(View.GONE);
        }else {
            if (btPrevious.getVisibility() == View.INVISIBLE || btPrevious.getVisibility() == View.GONE) {
                btPrevious.setVisibility(View.VISIBLE);
            }
        }
    }
    private void ShowHideBTNext() {
        if (verseNo.equals(String.valueOf(lastFile))){
            btNext.setVisibility(View.GONE);
        }else {
            if (btNext.getVisibility() == View.INVISIBLE || btNext.getVisibility() == View.GONE) {
                btNext.setVisibility(View.VISIBLE);
            }
        }
    }
    private void initTextSize(float view) {
        verseSText.setTextSize(view);
        verseBSText.setTextSize(view);
        verseBText.setTextSize(view);

        SharedPreferences.Editor editor = prefsVerse.edit();
        editor.putFloat("textSizeV", view);
        editor.apply();

    }
    private void LoadList(String name) {
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open(name+".json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            jsonArrayList = new JSONArray(stringBuilder.toString());
            int count = jsonArrayList.length();
           stopDialog();
            lastFile =count;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            stopDialog();

        }
    }
    private void LoadListFull(String newString) {
        for (int i = 0; i < jsonArrayList.length(); i++) {
            try {
                jsonElement = jsonArrayList.getJSONObject(i);
                int length = jsonArrayList.length();
                if (jsonElement.getString("serial").equals(newString)) {
                    if (jsonElement != null) {
                        String serial = jsonElement.getString("serial");
                        String verseS = jsonElement.getString("verseS");
                        String verseBS = jsonElement.getString("verseBS");
                        String verseB = jsonElement.getString("verseB");
                        String banglaNumber = BanglaNumberUtils.toBanglaNumber(Integer.parseInt(serial));
                        allHideSetupLoad();
                        if (chapterNo.equals("19")){
                            bookBox.setVisibility(View.GONE);
                            verseText.setText(titleVerse+banglaNumber);
                            bengaliTitle.setText(R.string.bangla_unubad);
                        }else {
                            if (i == length - 1) {
                                bookBox.setVisibility(View.GONE);
                                songskritLin.setVisibility(View.GONE);
                                banglaPodohLin.setVisibility(View.GONE);
                                verseText.setText(modifiedChapterName);
                                bengaliTitle.setText("সার-সংক্ষেপ");
                            }else {
                                if (bookBox.getVisibility() == View.GONE){
                                    bookBox.setVisibility(View.VISIBLE);
                                    if (checkS.equals("Gone")){
                                        songskritLin.setVisibility(View.GONE);
                                    }else {
                                        songskritLin.setVisibility(View.VISIBLE);
                                    }
                                    if (checkB.equals("Gone")){
                                        banglaPodohLin.setVisibility(View.GONE);
                                    }else {
                                        banglaPodohLin.setVisibility(View.VISIBLE);
                                    }
                                }
                                verseText.setText(titleVerse+banglaNumber);
                                bengaliTitle.setText(R.string.bangla_unubad);
                            }
                        }
                        verseNo = serial;
                        checkBookButton();

                        verseSText.setText(verseS);
                        verseBSText.setText(verseBS);
                        verseBText.setText(verseB);

                    } else {
                        System.out.println("Element not found.");
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void allHideSetupLoad() {
    }


    @Override
    public boolean onDown(@NonNull MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(@NonNull MotionEvent motionEvent, @NonNull MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float vX, float vY) {
        boolean result = false;
        try {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(vX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX < 0) {
                        int value = Integer.parseInt(verseNo);
                        if (value!=lastFile){
                            value++;
                            String newString = String.valueOf(value);
                            verseNo = newString;
                            LoadListFull(verseNo);
                            ShowHideBTPrevise();
                            ShowHideBTNext();
                        }else {
                            System.out.println("Element not found.");
                        }
                    } else {
                        int value = Integer.parseInt(verseNo);
                        if (value ==1){
                            System.out.println("Element not found.");

                        }else {
                            value--;
                            String newString = String.valueOf(value);
                            verseNo = newString;
                            LoadListFull(verseNo);
                            ShowHideBTPrevise();
                            ShowHideBTNext();
                        }
                    }
                    result = true;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
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
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) { int result = textToSpeech.setLanguage(new Locale("bn_IN"));

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("MyActivity", "Bangla language not supported");
            } else {
                // Set voice to male Bangla
                Locale bangla = new Locale("bn", "BD");
                Voice maleVoice = new Voice("bn-bd-x-ism-local", bangla, Voice.QUALITY_HIGH, Voice.LATENCY_NORMAL, false, null);
                textToSpeech.setSpeechRate(0.8f);
                textToSpeech.setLanguage(bangla);
                textToSpeech.setVoice(maleVoice);
            }
        } else {
            Log.e("MyActivity", "TextToSpeech initialization failed");
        }

    }
    private void speakText(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "messageId");
    }

}