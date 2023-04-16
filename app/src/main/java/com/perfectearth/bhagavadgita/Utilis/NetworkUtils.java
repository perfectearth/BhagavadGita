package com.perfectearth.bhagavadgita.Utilis;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.AsyncTask;

public class NetworkUtils {

    private static final String TEST_URL = "https://www.google.com/";

    public static void isNetworkAvailable(Context context, OnNetworkCheckListener listener) {
        new NetworkCheckTask(listener).execute(context);
    }

    private static class NetworkCheckTask extends AsyncTask<Context, Void, Integer> {
        private OnNetworkCheckListener listener;
        private Context mContext;

        public NetworkCheckTask(OnNetworkCheckListener listener) {
            this.listener = listener;
        }

        @Override
        protected Integer doInBackground(Context... contexts) {
            mContext = contexts[0];

            ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                // No active network connection
                return 0;
            } else {
                try {
                    URL url = new URL(TEST_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("HEAD");
                    connection.setConnectTimeout(2000); // Timeout after 2 seconds
                    int responseCode = connection.getResponseCode();
                    return responseCode;
                } catch (IOException e) {
                    CustomProgress.hideProgressBar();
                    e.printStackTrace();
                    // There was an error making the request, assume no internet
                    return -1;
                }
            }
        }
        @Override
        protected void onPostExecute(Integer result) {
            CustomProgress.hideProgressBar();
            if (result == 0) {
                // No active network connection
                if (listener != null) {
                    listener.onNetworkCheck(false);
                }
            } else if (result == -1) {
                // Error making request
                if (listener != null) {
                    listener.onNetworkCheck(false);
                }
            } else if (result == HttpURLConnection.HTTP_OK) {
                // Network is available
                if (listener != null) {
                    listener.onNetworkCheck(true);
                }
            } else {
                // Server response code not HTTP_OK
                if (listener != null) {
                    listener.onNetworkCheck(false);
                }
            }
        }
    }

    public interface OnNetworkCheckListener {
        void onNetworkCheck(boolean isNetworkAvailable);
    }
}
