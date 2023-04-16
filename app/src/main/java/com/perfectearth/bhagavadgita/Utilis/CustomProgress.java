package com.perfectearth.bhagavadgita.Utilis;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.perfectearth.bhagavadgita.R;

public class CustomProgress extends Dialog {

    private static CustomProgress mCustomProgressbar;
    private TextView mLoadingText;
    private OnDismissListener mOnDismissListener;

    private CustomProgress(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.wait_dialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mLoadingText = findViewById(R.id.loding_text);
    }

    public CustomProgress(Context context, Boolean instance) {
        super(context);
        mCustomProgressbar = new CustomProgress(context);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(this);
        }
    }

    public static void showProgressBar(Context context, boolean cancelable) {
        showProgressBar(context, cancelable, null);
    }

    public static void showProgressBar(Context context, boolean cancelable, String message) {
        if (mCustomProgressbar != null && mCustomProgressbar.isShowing()) {
            mCustomProgressbar.cancel();
        }
        mCustomProgressbar = new CustomProgress(context);
        mCustomProgressbar.setCancelable(cancelable);
        mCustomProgressbar.setLoadingText(message);
        mCustomProgressbar.show();
    }

    public static void showProgressBar(Context context, OnDismissListener listener) {
        if (mCustomProgressbar != null && mCustomProgressbar.isShowing()) {
            mCustomProgressbar.cancel();
        }
        mCustomProgressbar = new CustomProgress(context);
        mCustomProgressbar.setListener(listener);
        mCustomProgressbar.setCancelable(true);
        mCustomProgressbar.show();
    }

    public static void hideProgressBar() {
        if (mCustomProgressbar != null) {
            mCustomProgressbar.dismiss();
        }
    }

    private void setListener(OnDismissListener listener) {
        mOnDismissListener = listener;
    }

    private void setLoadingText(String message) {
        if (mLoadingText != null && message != null) {
            mLoadingText.setText(message);
        }
    }

    public static void showListViewBottomProgressBar(View view) {
        if (mCustomProgressbar != null) {
            mCustomProgressbar.dismiss();
        }

        view.setVisibility(View.VISIBLE);
    }

    public static void hideListViewBottomProgressBar(View view) {
        if (mCustomProgressbar != null) {
            mCustomProgressbar.dismiss();
        }

        view.setVisibility(View.GONE);
    }

    public void showProgress(Context context, boolean cancelable, String message) {
        if (mCustomProgressbar != null && mCustomProgressbar.isShowing()) {
            mCustomProgressbar.cancel();
        }
        mCustomProgressbar.setCancelable(cancelable);
        mCustomProgressbar.setLoadingText(message);
        mCustomProgressbar.show();
    }
}
