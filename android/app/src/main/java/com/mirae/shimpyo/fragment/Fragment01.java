package com.mirae.shimpyo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mirae.shimpyo.R;
import com.mirae.shimpyo.activity.ActivityQA;

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

        return view;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
        TextView textViewQuestion = view.findViewById(R.id.textViewQuestion);
        textViewQuestion.setText(no + "님 " + textViewQuestion.getText());
    }
}
