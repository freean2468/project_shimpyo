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
import com.mirae.shimpyo.helper.Diary;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Fragment03Ver2 extends Fragment {
    private View view;
    private TextView textViewMonthYear;
    private RecyclerView recyclerViewCalendar;
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
        buttonPreviousMonth.setOnClickListener(v -> previousMonthAction());
        //달력에서 "->"버튼 이벤트처리 함수
        buttonNextMonth.setOnClickListener(v -> nextMonthAction());
        
        //초기화하는 함수
        initWidgets();
        //localDate를 지금으로 설정
        localDateSelect = LocalDate.now();
        textViewMonthYear.setText(monthYearFromDate(localDateSelect));

        //layoutManager를 grid로 설정, 달력이 7열이며 칸이 일정해야하기 때문에
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(view.getContext(), 7);
        recyclerViewCalendar.setLayoutManager(layoutManager);
        recyclerViewCalendar.setAdapter(AdapterCalendar.getInstance(factoryDayInMonthArray(localDateSelect)));

        return view;
    }//end of onCreate

    //초기화
    private void initWidgets() {
        recyclerViewCalendar = view.findViewById(R.id.recyclerViewCalendar);
        textViewMonthYear = view.findViewById(R.id.textViewMonthYear);
    }

    private void setMonthView(LocalDate date) {
        textViewMonthYear.setText(monthYearFromDate(date));

        /**
         * 매번 AdapterCalendar를 만들고 셋팅하는 건 adapter 사용 취지에 어긋난다.
         * Notify를 통해 데이터만 바꾸고 데이터를 바탕으로 뷰를 갱신해야 한다.
         */
        changeDayInMonthArray(date);
    }

    //한달 안에 각각의 일수를 arrayList로 객체 저장하는 함수
    private ArrayList<Diary> factoryDayInMonthArray(LocalDate date) {
        ArrayList<Diary> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = date.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        int dayOfYear = firstOfMonth.getDayOfYear();

        /**
         * 여기서 캐싱을 할 수 있겠다.
         */
        //달력이 최소 4주에서 6주까지 필요하기(1일이 토요일) 때문에 최대가 42일
        for(int i = 1; i <=42 ;i++){
            int position = i-1;
            if(i <= dayOfWeek || i> daysInMonth + dayOfWeek) {
                daysInMonthArray.add(new Diary(
                        Diary.NOT_EXISTS,
                        view.getContext(),
                        (i - dayOfWeek),
                        position,
                        dayOfYear,
                        "",
                        null));
            } else {
                daysInMonthArray.add(new Diary(
                        Diary.UNKNOWN,
                        view.getContext(),
                        (i - dayOfWeek),
                        position,
                        dayOfYear,
                        "",
                        null));
                dayOfYear += 1;
            }

        }//end of for
        return daysInMonthArray;
    }

    private void changeDayInMonthArray(LocalDate date) {
        ArrayList<Diary> daysInMonthArray = AdapterCalendar.getInstance(null).getDaysOfMonth();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = date.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        int dayOfYear = firstOfMonth.getDayOfYear();

        /**
         * 여기서 캐싱을 할 수 있겠다.
         */
        //달력이 최소 4주에서 6주까지 필요하기(1일이 토요일) 때문에 최대가 42일
        for(int i = 1; i <=42 ;i++){
            int position = i-1;
            if(i <= dayOfWeek || i> daysInMonth + dayOfWeek) {
                daysInMonthArray.get(position).update(
                                Diary.NOT_EXISTS,
                                view.getContext(),
                                (i - dayOfWeek),
                                position,
                                dayOfYear,
                                "",
                                null);
            } else {
                daysInMonthArray.get(position).update(
                                Diary.UNKNOWN,
                                view.getContext(),
                                (i - dayOfWeek),
                                position,
                                dayOfYear,
                                "",
                                null);
                dayOfYear += 1;
            }
            AdapterCalendar.getInstance(null).notifyItemChanged(position);
        }//end of for
    }

    //로컬(대한민국으로 설정)시간으로 설정
    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM YYYY");
        return date.format(formatter);
    }

    //달력이 이전 달로 하나씩 넘어가는 기능
    public void previousMonthAction() {
        localDateSelect = localDateSelect.minusMonths(1);
        limitToThisYear();
        setMonthView(localDateSelect);
    }

    //달력이 다음 달로 하나씩 넘어가는 기능
    public void nextMonthAction() {
        localDateSelect = localDateSelect.plusMonths(1);
        limitToThisYear();
        setMonthView(localDateSelect);
    }

    private void limitToThisYear() {
        Button buttonPreviousMonth = view.findViewById(R.id.buttonPreviousMonth);
        Button buttonNextMonth = view.findViewById(R.id.buttonNextMonth);
        int nextMonth = localDateSelect.plusMonths(1).getMonthValue();
        int previousMonth = localDateSelect.minusMonths(1).getMonthValue();
        final int DEC = 12;
        final int JAN = 1;

        buttonNextMonth.setVisibility(View.VISIBLE);
        buttonPreviousMonth.setVisibility(View.VISIBLE);

        if (nextMonth == JAN) {
            buttonNextMonth.setVisibility(View.INVISIBLE);
        } else if (previousMonth == DEC) {
            buttonPreviousMonth.setVisibility(View.INVISIBLE);
        }
    }

    public LocalDate getLocalDateSelect() { return localDateSelect; }
}//end of class