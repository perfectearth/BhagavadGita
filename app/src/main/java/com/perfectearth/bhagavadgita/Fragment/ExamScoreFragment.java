package com.perfectearth.bhagavadgita.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.perfectearth.bhagavadgita.R;

public class ExamScoreFragment extends Fragment {

    public ExamScoreFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exam_score, container, false);
    }
}