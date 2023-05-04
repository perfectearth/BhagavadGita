package com.perfectearth.bhagavadgita.Adapter;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.perfectearth.bhagavadgita.AdapterItem.BookMarkItem;
import com.perfectearth.bhagavadgita.AdapterItem.ItemScore;
import com.perfectearth.bhagavadgita.R;
import com.perfectearth.bhagavadgita.Utilis.ChapterAll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {

    private Context scoreContext;
    private List<ItemScore> itemScoresList;

    public ScoreAdapter(Context scoreContext, List<ItemScore> itemScoresList) {
        this.scoreContext = scoreContext;
        this.itemScoresList = itemScoresList;
    }

    @NonNull
    @Override
    public ScoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz_score, parent, false);
        return new ScoreAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreAdapter.ViewHolder holder, int position) {
        List<ItemScore> itemScoresListSorted = new ArrayList<>(itemScoresList);
        Collections.sort(itemScoresListSorted, new Comparator<ItemScore>() {
            @Override
            public int compare(ItemScore o1, ItemScore o2) {
                return Integer.parseInt(o1.getScoreChapter()) - Integer.parseInt(o2.getScoreChapter());
            }
        });

        ItemScore itemScore = itemScoresListSorted.get(position);
        String number = itemScore.getScoreChapter();
        ChapterAll chapters = new ChapterAll();
        List<String> chapterNames = chapters.getChapterNames();
        int chapterNumber = Integer.parseInt(number);
        if (chapterNumber >= 1 && chapterNumber <= chapterNames.size()) {
            String chapterName = chapterNames.get(chapterNumber - 1);
            holder.chapterName.setText(chapterName);
        }
        int correctAns = 0;
        int check = Integer.parseInt(itemScore.getScoreCorrect());
        correctAns += check;
        int level = (correctAns / 100) + 1;
        if (correctAns % 100 == 0) {
            holder.scoreBar.setProgress(0);
        }
        holder.scoreText.setText("Score "+correctAns*5);
        holder.scoreBar.setProgress(correctAns % 100);
        holder.scoreDetails.setText("Current Level " + level);
        holder.progressText.setText(correctAns % 100 + "%");
    }


        @Override
    public int getItemCount() {
        return itemScoresList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView  chapterName,scoreText ,scoreDetails,progressText;
        ProgressBar scoreBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chapterName = itemView.findViewById(R.id.text_score_chapter);
            scoreText = itemView.findViewById(R.id.score_text);
            scoreDetails = itemView.findViewById(R.id.score_details);
            progressText = itemView.findViewById(R.id.score_progress_text);
            scoreBar = itemView.findViewById(R.id.progress_score);
        }
    }
}
