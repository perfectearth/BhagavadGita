package com.perfectearth.bhagavadgita.Adapter;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.perfectearth.bhagavadgita.AdapterItem.ChapterItem;
import com.perfectearth.bhagavadgita.R;
import com.perfectearth.bhagavadgita.Utilis.ChapterAll;

import java.security.AccessControlContext;
import java.util.ArrayList;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private Context mContext;
    private final ArrayList<ChapterItem> chapterItem;
    private final OnItemClickListener mListener;


    public ChapterAdapter(Context mContext, ArrayList<ChapterItem> chapterItem, OnItemClickListener mListener) {
        this.mContext = mContext;
        this.chapterItem = chapterItem;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chapters_item, parent, false);
        return new ChapterViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        ChapterItem currentItem = chapterItem.get(position);
        String number = String.valueOf(position+1);

        ChapterAll chapters = new ChapterAll();
        List<String> chapterNames = chapters.getChapterNames();

        int chapterNumber = Integer.parseInt(number);

        if (chapterNumber >= 1 && chapterNumber <= chapterNames.size()) {
            String chapterName = chapterNames.get(chapterNumber - 1);
            holder.serialChapter.setText(chapterName);
        }
        String count = currentItem.getChapterCount();
        String lastWord = count.substring(count.lastIndexOf(" ") + 1);

        if (lastWord.equals("শ্লোক।")) {
            Drawable iconDrawable = mContext.getResources().getDrawable(R.drawable.list_icon);
            holder.iconAll.setImageDrawable(iconDrawable);
        } else {
            Drawable iconDrawable = mContext.getResources().getDrawable(R.drawable.icon_check);
            holder.iconAll.setImageDrawable(iconDrawable);
        }

        holder.countChapter.setText(currentItem.getChapterCount());
        holder.nameChapter.setText(currentItem.getChapterName());


    }

    @Override
    public int getItemCount() {
        return chapterItem.size();
    }
    public interface OnItemClickListener {
        void onItemClick(String position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ChapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nameChapter;
        public ImageView iconAll;
        public TextView serialChapter;
        public TextView countChapter;
        public CardView chapterClick;

        OnItemClickListener mListener;
        public ChapterViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            nameChapter = itemView.findViewById(R.id.chapter_name);
            serialChapter = itemView.findViewById(R.id.chapter_serial);
            countChapter = itemView.findViewById(R.id.chapter_count);
            chapterClick = itemView.findViewById(R.id.chapter_click);
            iconAll = itemView.findViewById(R.id.chapter_icon);
            mListener = listener;
            chapterClick.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            String po = String.valueOf(position+1);
            if (mListener != null && position != RecyclerView.NO_POSITION) {
                mListener.onItemClick(po);
            }
        }
    }
}
