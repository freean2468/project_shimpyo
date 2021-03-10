package com.mirae.shimpyo.adaptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.mirae.shimpyo.fragment.Fragment01;
import com.mirae.shimpyo.fragment.Fragment02;
import com.mirae.shimpyo.fragment.Fragment03;

public class AdaptorViewPager extends FragmentPagerAdapter {

    public AdaptorViewPager(@NonNull FragmentManager fm) {
        super(fm);
    }

    private final static int FRAGMENT_01 = 0;
    private final static int FRAGMENT_02 = 1;
    private final static int FRAGMENT_03 = 2;
    private final static int FRAGMENT_COUNT = 3;

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case FRAGMENT_01: return Fragment01.getInstance();
            case FRAGMENT_02: return Fragment02.getInstance();
            case FRAGMENT_03: return Fragment03.getInstance();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case FRAGMENT_01: return "📷";
            case FRAGMENT_02: return "❔";
            case FRAGMENT_03: return "🌙";
            default: return null;
        }
    }
}
