package com.perfectearth.bhagavadgita.Adapter;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.perfectearth.bhagavadgita.AdapterItem.Question;
import com.perfectearth.bhagavadgita.R;
import java.util.ArrayList;
import java.util.Collections;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ExamViewHolder> {

    private ArrayList<Question> questionList;
    private Context context;
    private ItemOnClickListener onClickListener;


    public ExamAdapter(ArrayList<Question> questionList, Context context, ItemOnClickListener onClickListener) {
        this.questionList = questionList;
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.exam_item, parent, false);
        return new ExamViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {

        Question examItem = questionList.get(position);
        String serial = String.valueOf(position + 1);
        String ansCheck = examItem.getCorrectAnswer();
        holder.textQuestion.setText(examItem.getQuestion());
        holder.serialQuestion.setText(serial);
        ArrayList<String> answers = new ArrayList<>();
        answers.add(examItem.getAnsA());
        answers.add(examItem.getAnsB());
        answers.add(examItem.getAnsC());
        answers.add(examItem.getAnsD());
        Collections.shuffle(answers);

        holder.btnA.setText(answers.get(0));
        holder.btnB.setText(answers.get(1));
        holder.btnC.setText(answers.get(2));
        holder.btnD.setText(answers.get(3));

        RadioButton[] radioButtons = {holder.btnA, holder.btnB, holder.btnC, holder.btnD};
        for (RadioButton radioButton : radioButtons) {
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.d(TAG, "onCheckedChanged: " + buttonView.getText());
                    String answer = (String) radioButton.getText();
                    if (answer.equals(ansCheck)){
                        onClickListener.onItemClick(1);
                    }else {
                        onClickListener.onItemClick(0);
                    }
                    for (RadioButton rb : radioButtons) {
                        if (rb != buttonView) {
                            rb.setEnabled(!isChecked);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface ItemOnClickListener{
        void onItemClick(int correctAns);
    }
    public class ExamViewHolder extends RecyclerView.ViewHolder {

        private TextView textQuestion,serialQuestion;
        private RadioButton btnA,btnB,btnC,btnD;
        public ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            textQuestion = itemView.findViewById(R.id.exam_question);
            serialQuestion = itemView.findViewById(R.id.exam_serial);
            btnA = itemView.findViewById(R.id.radio_ans_a);
            btnB = itemView.findViewById(R.id.radio_ans_b);
            btnC = itemView.findViewById(R.id.radio_ans_c);
            btnD = itemView.findViewById(R.id.radio_ans_d);

        }
    }

}
