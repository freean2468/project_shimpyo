package com.mirae.shimpyo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Fragment02 extends Fragment {
    private View view;

    public static Fragment02 newInstance(){
        Fragment02 fragment02 = new Fragment02();
        return fragment02;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment02, container, false);

        return view;
    }
}
