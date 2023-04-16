package com.perfectearth.bhagavadgita.Utilis;


import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.perfectearth.bhagavadgita.R;

public class BannerDialog extends DialogFragment {

    private OnDialogButtonClickListener mListener;

    public void setOnDialogButtonClickListener(OnDialogButtonClickListener listener) {
        mListener = listener;
    }




    public interface OnDialogButtonClickListener {
        void onYesButtonClick();
        void onNoButtonClick();
    }
    private String textDetails;
    public void setTextD(String setTextD) {
        textDetails = setTextD;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.banner_dialog, null);
        // Get references to the UI elements
        ImageView mIcon = view.findViewById(R.id.banner_icon);
        TextView mMessage = view.findViewById(R.id.b_message);
        Button mYesButton = view.findViewById(R.id.btn_yes);
        Button mNoButton = view.findViewById(R.id.btn_no);
        mMessage.setText(R.string.gita_name);
        mMessage.setText(textDetails);

        // Set the icon if one was provided
        Bundle bundle = getArguments();
        if (bundle != null) {
            int iconResId = bundle.getInt("icon_res_id");
            mIcon.setImageResource(iconResId);
        }

        // Set a click listener for the Yes button
        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onYesButtonClick();
                }
                dismiss();
            }
        });

        // Set a click listener for the No button
        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onNoButtonClick();
                }
                dismiss();
            }
        });

        // Set the view for the dialog
        builder.setView(view);

        // Return the built dialog
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
        }

        // Set the dimensions and animations for the dialog
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setWindowAnimations(R.style.DialogAnimation);
        }
    }

}
