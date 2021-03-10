package com.mirae.shimpyo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Fragment03 extends Fragment {
    private View view;

    public static Fragment03 newInstance(){
        Fragment03 fragment03 = new Fragment03();
        return fragment03;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment03, container, false);

        return view;
    }
}
