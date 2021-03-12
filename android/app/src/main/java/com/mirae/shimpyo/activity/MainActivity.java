package com.mirae.shimpyo.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mirae.shimpyo.R;
import com.mirae.shimpyo.adaptor.CalendarAdapter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener{
    private TextView TextViewMonthYear;
    private RecyclerView RectclerViewCalendar;
    private LocalDate LocalDateSelect;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
        LocalDateSelect = LocalDate.now();
        setMonthView();

    }//end of onCreate

    private void initWidgets() {
        RectclerViewCalendar = findViewById(R.id.RecyclerViewCalendar);
        TextViewMonthYear = findViewById(R.id.TextViewMonthYear);
    }

    private void setMonthView() {
        TextViewMonthYear.setText(monthYearFromDate(LocalDateSelect));
        ArrayList<String> daysInMonth = dayInMonthArray(LocalDateSelect);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        RectclerViewCalendar.setLayoutManager(layoutManager);
        RectclerViewCalendar.setAdapter(calendarAdapter);

    }

    private ArrayList<String> dayInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = LocalDateSelect.withDayOfMonth(1);
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
        LocalDateSelect = LocalDateSelect.minusMonths(1);
        setMonthView();
    }

    public void nextMouthAction(View view) {
        LocalDateSelect = LocalDateSelect.plusMonths(1);
        setMonthView();
    }

//    @Override
//    public void onItemClick(int position, String dayText) {
//        if(dayText.equals("")){
//            String message = "Selected Date " + dayText + " " + monthYearFromDate(LocalDateSelect);
//            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
//        }
//    }


}//end of class