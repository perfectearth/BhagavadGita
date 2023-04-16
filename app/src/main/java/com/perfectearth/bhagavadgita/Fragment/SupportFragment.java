package com.perfectearth.bhagavadgita.Fragment;


import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.perfectearth.bhagavadgita.R;


public class SupportFragment extends Fragment {

    private LinearLayout chatLayout;
    private TextInputLayout messageEditText;
    private Spinner mySpinner;

    public SupportFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View support =  inflater.inflate(R.layout.fragment_support, container, false);

        chatLayout = support.findViewById(R.id.chat_add);
        messageEditText = support.findViewById(R.id.send_edit);


        mySpinner = support.findViewById(R.id.spinner_support);
        String[] items = {"App Design", "Chapter Problem", "Verse Problem", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        mySpinner.setAdapter(adapter);

        messageEditText.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        return support;
    }

    private void sendMessage() {

        String messageText = messageEditText.getEditText().toString().trim();

        if (!TextUtils.isEmpty(messageText)) {
            // Add sender bubble to layout
            addSenderBubble(messageText);

            // Clear message edit text
            messageEditText.setHelperText("");

            // Simulate receiver typing
            simulateTyping();

            // Simulate receiver response
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String responseText = "This is a sample response.";
                    addReceiverBubble(responseText);
                }
            }, 1500);
        }
    }

    private void simulateTyping() {
        addReceiverBubble("Typing...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                removeLastBubble();
            }
        }, 1500);
    }

    private void removeLastBubble() {
        int childCount = chatLayout.getChildCount();
        if (childCount > 0) {
            chatLayout.removeViewAt(childCount - 1);
        }
    }

    private void addReceiverBubble(String responseText) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View bubbleView = inflater.inflate(R.layout.chat_layout_receiver, null);
        TextView messageTextView = bubbleView.findViewById(R.id.message_text_receiver);
        messageTextView.setText(responseText);
        chatLayout.addView(bubbleView);
    }

    private void addSenderBubble(String messageText) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View bubbleView = inflater.inflate(R.layout.chat_layout_sender, null);
        TextView messageTextView = bubbleView.findViewById(R.id.message_text_sender);
        messageTextView.setText(messageText);
        chatLayout.addView(bubbleView);
    }
}
