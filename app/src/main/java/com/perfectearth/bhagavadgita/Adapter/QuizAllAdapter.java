package com.perfectearth.bhagavadgita.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.perfectearth.bhagavadgita.AdapterItem.QuizItemAll;
import com.perfectearth.bhagavadgita.R;
import com.perfectearth.bhagavadgita.Utilis.OrdinalUtilis;

import java.util.List;

public class QuizAllAdapter extends RecyclerView.Adapter<QuizAllAdapter.ViewHolder> {

    Context mContext;
    private List<QuizItemAll> quizItemAllList;

    public QuizAllAdapter(Context mContext, List<QuizItemAll> quizItemAllList) {
        this.mContext = mContext;
        this.quizItemAllList = quizItemAllList;
    }

    @NonNull
    @Override
    public QuizAllAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View allView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz_all, parent, false);
        return new QuizAllAdapter.ViewHolder(allView);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizAllAdapter.ViewHolder holder, int position) {
        QuizItemAll quizItemAll = quizItemAllList.get(position);
        String workSerial = quizItemAll.getSerial();
        String suffix;
        String markText;
        if (workSerial.equals("4")) {
            suffix = OrdinalUtilis.getOrdinalSuffix(position + 4);
            int score = Integer.parseInt(quizItemAll.getScore());
            markText = "Score " + (score * 5);
        } else {
            suffix = OrdinalUtilis.getOrdinalSuffix(position + 1);
            int score = Integer.parseInt(quizItemAll.getScore());
            int total = Integer.parseInt(quizItemAll.getTotal());
            double resultTotal = ((double) score / total) * 100;
            markText = "Mark " + Math.round(resultTotal);
        }

        SpannableString spannableString = new SpannableString(markText);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), markText.indexOf(" "), markText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int color = ContextCompat.getColor(mContext, R.color.teal_700);
        spannableString.setSpan(new ForegroundColorSpan(color), markText.indexOf(" "), markText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.scoreText.setText(spannableString);
        holder.serialText.setText(suffix);

        String name = quizItemAll.getName();
        char firstChar = name.charAt(0);
        String firstLetter = String.valueOf(firstChar);
        holder.nameText.setText(name);
        holder.nameWord.setText(firstLetter);

    }

    @Override
    public int getItemCount() {
        return quizItemAllList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameText , scoreText, nameWord,serialText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.quiz_user_name);
            scoreText = itemView.findViewById(R.id.quiz_user_score);
            nameWord = itemView.findViewById(R.id.quiz_card_text);
            serialText = itemView.findViewById(R.id.quiz_score_serial);
        }
    }
}
