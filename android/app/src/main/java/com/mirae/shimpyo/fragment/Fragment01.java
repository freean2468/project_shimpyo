package com.mirae.shimpyo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mirae.shimpyo.R;
import com.mirae.shimpyo.activity.ActivityQA;
import com.mirae.shimpyo.object.ObjectVolley;

public class Fragment01 extends Fragment {
    private View view;
    private String no;
    private int dayOfYear;
    private String answer;
    private byte[] photo;

    public static Fragment01 instance = null;

    private Fragment01() {

    }

    public static Fragment01 getInstance(){
        if (instance == null) {
            instance = new Fragment01();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment01, container, false);

        EditText editTextAnswer = view.findViewById(R.id.editTextAnswer);
        Button buttonAnswer = view.findViewById(R.id.buttonAnswer);

        ObjectVolley objectVolley = ObjectVolley.getInstance(view.getContext());

        buttonAnswer.setOnClickListener((v) -> {
            objectVolley.requestAnswer(
                no,
                dayOfYear,
                editTextAnswer.getText().toString(),
                this.photo,
                new ObjectVolley.RequestAnswerListener() {
                    @Override
                    public void jobToDo() {
                        Log.e(getString(R.string.TAG_SERVER), "응답 성공");
                    }
                },
                error -> { Log.e(getString(R.string.TAG_SERVER), error.toString()); }
            );
        });

        return view;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
        TextView textViewQuestion = view.findViewById(R.id.textViewQuestion);
        textViewQuestion.setText(this.no + "님 " + textViewQuestion.getText());
    }

    public void setDayOfYear(int dayOfYear) {
        this.dayOfYear = dayOfYear;
        TextView textViewDayOfYear = view.findViewById(R.id.textViewDayOfYear);
        textViewDayOfYear.setText(textViewDayOfYear.getText().toString() + "(" + String.valueOf(this.dayOfYear) + ")");
    }

    public void setAnswer(String answer) {
        this.answer = answer;
        Log.d("debug", this.answer);
        EditText editTextAnswer = view.findViewById(R.id.editTextAnswer);
        editTextAnswer.setText(this.answer);
    }

    public void setPhoto(byte[] photo) { this.photo = photo; }
}
