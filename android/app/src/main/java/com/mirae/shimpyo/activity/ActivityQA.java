package com.mirae.shimpyo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;
import com.mirae.shimpyo.adaptor.AdaptorViewPager;
import com.mirae.shimpyo.R;
import com.mirae.shimpyo.fragment.Fragment01;
import com.mirae.shimpyo.object.ObjectVolley;

import java.util.Calendar;

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
        String no = intent.getStringExtra("no");
        int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        Log.i("WEB_SERVER", "no : " + no);

        ObjectVolley objectVolley = ObjectVolley.getInstance(this);

        objectVolley.requestKakaoLogin(
            no,
            dayOfYear,
            new ObjectVolley.RequestLoginListener() {
                @Override public void jobToDo() {
                    Fragment01 fragment01 = Fragment01.getInstance();
                    fragment01.setNo(this.no);
                    fragment01.setDayOfYear(this.dayOfYear);
                    fragment01.setAnswer(this.answer);
                    fragment01.setPhoto(this.photo);
                }
            },
            error -> { Log.e(getString(R.string.TAG_SERVER), "RequestLogin error"); }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("CYCLE", "onResume");
    }


    /**
     * service 시 주석을 풀면 로그인화면이 아니라 Home 화면으로 나가게 해준다.
     */
//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }
}