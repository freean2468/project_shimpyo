package com.mirae.shimpyo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mirae.shimpyo.R;

public class Fragment03 extends Fragment {
    private View view;

    public static Fragment03 instance = new Fragment03();

    private Fragment03() {

    }

    public static Fragment03 getInstance(){ return instance; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment03, container, false);

        return view;
    }
}
