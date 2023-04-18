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
        if (position== 0) {
            holder.itemView.setVisibility(View.GONE);
        }else {
            ItemScore itemScore = itemScoresList.get(position);
            holder.scoreText.setText(itemScore.getScoreTotal());
            holder.chapterName.setText(itemScore.getScoreChapter());
            holder.scoreDetails.setText(itemScore.getScoreCorrect());
        }
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
