package com.mirae.shimpyo.activity;

import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.mirae.shimpyo.adapter.AdapterViewPager;
import com.mirae.shimpyo.R;

/**
 *
 *
 * @author 허선영
 * 
 *
 */
public class ActivityQA extends ActivityNoSystemBar {
    private AdapterViewPager fragmentPagerAdapter;
    private String no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa);

        ViewPager viewPager = findViewById(R.id.ViewPager);
        fragmentPagerAdapter = new AdapterViewPager(getSupportFragmentManager());
        viewPager.setAdapter(fragmentPagerAdapter);

        Intent intent = getIntent();
        no = intent.getStringExtra("no");
    }

    /**
     * service 시 주석을 풀면 로그인화면이 아니라 Home 화면으로 나가게 해준다.
     *
     * @author 송훈일(freean2468@gmail.com)
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}