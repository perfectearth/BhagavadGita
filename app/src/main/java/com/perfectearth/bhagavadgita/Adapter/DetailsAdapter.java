package com.perfectearth.bhagavadgita.Adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.perfectearth.bhagavadgita.AdapterItem.VerseItem;
import com.perfectearth.bhagavadgita.Utilis.BanglaNumberUtils;
import com.perfectearth.bhagavadgita.R;
import java.util.ArrayList;


public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder> {

    private final ArrayList<VerseItem> verseItems;
    private final VerseClick verseClick;
    private float textSizeA;
    private Typeface typeface;

    public DetailsAdapter(ArrayList<VerseItem> verseItems, VerseClick verseClick, float textSizeA, Typeface typeface) {
        this.verseItems = verseItems;
        this.verseClick = verseClick;
        this.textSizeA = textSizeA;
        this.typeface = typeface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verse, parent, false);
        return new ViewHolder(view, verseClick);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VerseItem verseItem = verseItems.get(position);
        int po = Integer.parseInt(String.valueOf(position+1));
        String banglaNumber = BanglaNumberUtils.toBanglaNumber(po);
        if (position == getItemCount() - 1) { // check if it's the last item
            holder.textVerse.setText("সার-সংক্ষেপ");
        } else {
            holder.textVerse.setText("শ্লোক " + banglaNumber);
        }
        holder.textVerse.setTextSize(textSizeA);
        holder.textVerse.setTypeface(typeface);
        holder.textDetails.setText(verseItem.getVerseB());
        holder.textDetails.setTextSize(textSizeA);
        holder.textDetails.setTypeface(typeface);
    }

    @Override
    public int getItemCount() {
        return verseItems.size();
    }


    public interface VerseClick {
        void onItemClickV(String position);
    }
    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
        notifyDataSetChanged();
    }
    public void setTextSize(float textSize) {
        this.textSizeA = textSize;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textVerse;
        TextView textDetails;
        CardView verseCard;
        VerseClick verseClick;
        public ViewHolder(@NonNull View itemView, VerseClick click) {
            super(itemView);
            textVerse = itemView.findViewById(R.id.verse_serial);
            textDetails = itemView.findViewById(R.id.verse_details);
            verseCard = itemView.findViewById(R.id.verse_click);
            verseClick= click;
            verseCard.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            String verseSerial = String.valueOf(position+1);
            if (verseClick != null && position != RecyclerView.NO_POSITION) {
                verseClick.onItemClickV(verseSerial);
            }

        }
    }
}