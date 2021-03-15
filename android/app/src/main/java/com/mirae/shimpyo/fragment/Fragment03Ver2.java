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

    public static Fragment03Ver2 instance = null;

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

        buttonPreviousMonth.setOnClickListener(v -> previousMouthAction(v));
        buttonNextMonth.setOnClickListener(v -> nextMouthAction(v));

        initWidgets();
        localDateSelect = LocalDate.now();
        setMonthView();

        return view;
    }//end of onCreate

    private void initWidgets() {
        rectclerViewCalendar = view.findViewById(R.id.recyclerViewCalendar);
        textViewMonthYear = view.findViewById(R.id.textViewMonthYear);
    }

    private void setMonthView() {
        textViewMonthYear.setText(monthYearFromDate(localDateSelect));
        ArrayList<String> daysInMonth = dayInMonthArray(localDateSelect);

        AdapterCalendar adapterCalendar = new AdapterCalendar(daysInMonth);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(view.getContext(), 7);
        rectclerViewCalendar.setLayoutManager(layoutManager);
        rectclerViewCalendar.setAdapter(adapterCalendar);
    }

    private ArrayList<String> dayInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = localDateSelect.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <=42 ;i++){
            if(i <= dayOfWeek || i> daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
            }else{
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }

        }//end of for
        return daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM YYYY");
        return date.format(formatter);
    }

    public void previousMouthAction(View view) {
        localDateSelect = localDateSelect.minusMonths(1);
        setMonthView();
    }

    public void nextMouthAction(View view) {
        localDateSelect = localDateSelect.plusMonths(1);
        setMonthView();
    }
}//end of class