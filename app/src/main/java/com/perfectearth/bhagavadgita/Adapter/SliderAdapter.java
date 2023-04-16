package com.perfectearth.bhagavadgita.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.perfectearth.bhagavadgita.R;

public class SliderAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInfl;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    int image [] = {
            R.drawable.first_splash,
            R.drawable.secend_splash,
            R.drawable.three_splash
    };
    int headings[] = {
            R.string.heading_a,
            R.string.heading_b,
            R.string.heading_c
    };
    int description[] = {
            R.string.description_a,
            R.string.description_b,
            R.string.description_c
    };

    @Override
    public int getCount() {
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInfl = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInfl.inflate(R.layout.slide_layout, container,false);

        ImageView imageView = view.findViewById(R.id.slider_image);
        TextView heading = view.findViewById(R.id.slider_heading);
        TextView describ = view.findViewById(R.id.slider_desc);
        imageView.setImageResource(image[position]);
        heading.setText(headings[position]);
        describ.setText(description[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
