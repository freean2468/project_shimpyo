package com.mirae.shimpyo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mirae.shimpyo.R;
import com.mirae.shimpyo.adapter.AdapterCalendar;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Fragment03Ver2 extends Fragment {
    private View view;
    private TextView textViewMonthYear;
    private RecyclerView rectclerViewCalendar;
    private LocalDate localDateSelect;

    private static Fragment03Ver2 instance = null;

    private Fragment03Ver2() { }

    public static Fragment03Ver2 getInstance(){
        if (instance == null) {
            instance = new Fragment03Ver2();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment03_ver2, container, false);
        Button buttonPreviousMonth = view.findViewById(R.id.buttonPreviousMonth);
        Button buttonNextMonth = view.findViewById(R.id.buttonNextMonth);

        //달력에서 "<-"버튼 이벤트처리 함수
        buttonPreviousMonth.setOnClickListener(v -> previousMouthAction(v));
        //달력에서 "->"버튼 이벤트처리 함수
        buttonNextMonth.setOnClickListener(v -> nextMouthAction(v));

        //초기화하는 함수
        initWidgets();
        //localDate를 지금으로 설정
        localDateSelect = LocalDate.now();
        setMonthView();

        return view;
    }//end of onCreate

    //초기화
    private void initWidgets() {
        rectclerViewCalendar = view.findViewById(R.id.recyclerViewCalendar);
        textViewMonthYear = view.findViewById(R.id.textViewMonthYear);
    }

    private void setMonthView() {
        textViewMonthYear.setText(monthYearFromDate(localDateSelect));
        ArrayList<String> daysInMonth = dayInMonthArray(localDateSelect);

        AdapterCalendar adapterCalendar = new AdapterCalendar(daysInMonth);

        //layoutManager를 grid로 설정, 달력이 7열이며 칸이 일정해야하기 때문에
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(view.getContext(), 7);
        rectclerViewCalendar.setLayoutManager(layoutManager);
        rectclerViewCalendar.setAdapter(adapterCalendar);
    }

    //한달 안에 각각의 일수를 arrayList로 객체 저장하는 함수
    private ArrayList<String> dayInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = localDateSelect.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        //달력이 최소 4주에서 6주까지 필요하기(1일이 토요일) 때문에 최대가 42일
        for(int i = 1; i <=42 ;i++){
            if(i <= dayOfWeek || i> daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
            }else{
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }

        }//end of for
        return daysInMonthArray;
    }

    //로컬(대한민국으로 설정)시간으로 설정
    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM YYYY");
        return date.format(formatter);
    }

    //달력이 이전 달로 하나씩 넘어가는 기능
    public void previousMouthAction(View view) {
        localDateSelect = localDateSelect.minusMonths(1);
        setMonthView();
    }

    //달력이 다음 달로 하나씩 넘어가는 기능
    public void nextMouthAction(View view) {
        localDateSelect = localDateSelect.plusMonths(1);
        setMonthView();
    }
}//end of class