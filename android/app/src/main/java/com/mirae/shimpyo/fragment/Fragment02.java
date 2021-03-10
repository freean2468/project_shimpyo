package com.mirae.shimpyo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mirae.shimpyo.R;

public class Fragment02 extends Fragment {
    private View view;

    public static Fragment02 instance = new Fragment02();

    private Fragment02() {

    }

    public static Fragment02 getInstance(){ return instance; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment02, container, false);

        return view;
    }
}
