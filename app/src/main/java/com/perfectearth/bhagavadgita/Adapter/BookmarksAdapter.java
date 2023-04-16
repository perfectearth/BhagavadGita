package com.perfectearth.bhagavadgita.Adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.perfectearth.bhagavadgita.AdapterItem.BookMarkItem;
import com.perfectearth.bhagavadgita.Utilis.DBHelper;
import com.perfectearth.bhagavadgita.R;
import com.perfectearth.bhagavadgita.Utilis.BanglaNumberUtils;
import com.perfectearth.bhagavadgita.Utilis.ChapterAll;

import java.util.List;

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.ViewHolder> {

    private List<BookMarkItem> bookList;
    private Context context;
    private OnItemLongClickListener onItemLongClickListener;
    private ItemBookmarkClickListener bookListener;

    public BookmarksAdapter(List<BookMarkItem> bookList, Context context, OnItemLongClickListener onItemLongClickListener, ItemBookmarkClickListener bookListener) {
        this.bookList = bookList;
        this.context = context;
        this.onItemLongClickListener = onItemLongClickListener;
        this.bookListener = bookListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bookmark_item, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookMarkItem book = bookList.get(position);
        String text = book.getBookSerial();
        String text2 = book.getLastGita();
        String verseN = book.getLastGita();
        ChapterAll chapters = new ChapterAll();
        List<String> chapterNames = chapters.getChapterNames();
        int chapterNumber = Integer.parseInt(text);
        int verseNumber = Integer.parseInt(verseN);
        if (chapterNumber >= 1 && chapterNumber <= chapterNames.size()) {
            String chapterName = chapterNames.get(chapterNumber - 1);
            String banglaVerseNumber = BanglaNumberUtils.toBanglaNumber(verseNumber);
            String title = chapterName +"\n"+ "শ্লোক : "+ banglaVerseNumber;
            holder.titleTextView.setText(title);
        } else {
            holder.titleTextView.setText("No matching chapter name found");
        }
        if (text.equals("19")){
            Drawable drawable = context.getDrawable(R.drawable.text);
            holder.titleTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
        }
        holder.authorTextView.setText(book.getBookDetails());
        holder.bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookListener.onBookmarkClick(chapterNumber,verseNumber);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (text.equals("19")){
                    Log.e(TAG,"not get number");

                }else {
                    int position = holder.getAdapterPosition();
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.onItemLongClick(position);
                        DBHelper dbHelper = new DBHelper(context);
                        String alltext = text;

                        boolean success = dbHelper.deleteDataByTextValue(text+"n"+text2);
                        if (success) {
                            Log.d(TAG, "JSON object delete"+text+"n"+text2);

                        } else {
                            Log.d(TAG, "Failed to delete JSON ");
                        }
                        dbHelper.close();
                        return true;
                    }
                }
                return false;
            }
        });

        // Set other book data as needed
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
    public interface ItemBookmarkClickListener {
        void onBookmarkClick(int position, int verseCode);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView bookmarkButton;
        public TextView titleTextView;
        public TextView authorTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.book_serial);
            authorTextView = itemView.findViewById(R.id.book_details);
            bookmarkButton = itemView.findViewById(R.id.book_click);
            // Get other views as needed
        }
    }
}