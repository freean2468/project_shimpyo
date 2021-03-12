package com.mirae.shimpyo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.mirae.shimpyo.adaptor.AdaptorViewPager;
import com.mirae.shimpyo.R;
import com.mirae.shimpyo.fragment.Fragment01;
import com.mirae.shimpyo.object.ObjectVolley;

/**
 *
 *
 * @author 허선영
 *
 */
public class ActivityQA extends AppCompatActivity {
    private AdaptorViewPager fragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa);

        ViewPager viewPager = findViewById(R.id.ViewPager);
        fragmentPagerAdapter = new AdaptorViewPager(getSupportFragmentManager());

        TabLayout tabLayout = findViewById(R.id.TabLayout);
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        /**
         * ActivityLogin으로부터 회원번호를 받아 웹서버에 전달, 회원 정보를 가져온다.
         *
         * @author 송훈일(freean2468@gmail.com)
         */
        Intent intent = getIntent();
        long no = intent.getLongExtra("no", 0);

        Log.i("WEB_SERVER", "no : " + no);

        ObjectVolley objectVolley = ObjectVolley.getInstance(this);

        ActivityQA activity = this;
        objectVolley.requestKakaoLogin(
            Long.toString(no),
            new ObjectVolley.RequestLoginListener() {
                @Override public void jobToDo() {
                    Fragment01 fragment01 = Fragment01.getInstance();

                    fragment01.setNo(this.no);

                    Log.i("WEB_SERVER",this.no + "님 일년의 쉼표, " + this.dayOfYear + "일째 날입니다.");
                }
            },
            error -> { Toast.makeText(Fragment01.getInstance().getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show(); }
        );

    }
}