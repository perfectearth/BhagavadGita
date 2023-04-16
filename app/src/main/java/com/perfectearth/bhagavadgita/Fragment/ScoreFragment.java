package com.perfectearth.bhagavadgita.Fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.perfectearth.bhagavadgita.R;
public class ScoreFragment extends Fragment {

    public ScoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_score, container, false);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup overlayContainer = getActivity().findViewById(R.id.overlay_quiz);
        overlayContainer.removeViewAt(overlayContainer.getChildCount() - 1);
    }

}