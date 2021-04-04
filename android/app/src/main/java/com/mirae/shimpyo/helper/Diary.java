package com.mirae.shimpyo.helper;

import android.content.Context;
import android.util.Log;

import com.mirae.shimpyo.R;
import com.mirae.shimpyo.adapter.AdapterCalendar;
import com.mirae.shimpyo.fragment.Fragment01;
import com.mirae.shimpyo.object.ObjectVolley;

import java.util.LinkedList;

public class Diary {
    public static final int NOT_EXISTS = 0;
    public static final int UNKNOWN = 1;
    public static final int LOADED_SOME = 2;
    public static final int LOADED_NONE = 3;

    private int day;
    private int dayOfYear;
    private String answer;
    private byte[] photo;
    private int state;
    private int position;

    public Diary(int state, Context context, int day, int position, int dayOfYear, String answer, byte[] photo) {
        update(state, context, day, position, dayOfYear, answer, photo);
    }

    public void update(int state, Context context, int day, int position, int dayOfYear, String answer, byte[] photo) {
        this.day = day;
        this.dayOfYear = dayOfYear;
        this.position = position;
        this.state = state;
        this.answer = answer;
        this.photo = photo;

        if (state == Diary.UNKNOWN) {
            Fragment01 fragment01 = Fragment01.getInstance();
            String no = fragment01.getNo();

//            Log.d("debug", "no : " + no + ", day : " + day);
            ObjectVolley.getInstance(context).requestDiary(
                    no,
                    dayOfYear,
                    new ObjectVolley.RequestDiaryListener() {
                        @Override
                        public void jobToDo() {
                            Log.d(context.getString(R.string.tag_server), "day : " + day + ", position : " + getPosition() + ", answer : " + ", photo.length : " + photo.length);
                            setAnswer(this.answer);
                            setPhoto(this.photo);
                            setState(LOADED_SOME);
                            /**
                             * 한 번에 하나씩 넘겨도 adapter의 onBindViewHolder에서 List<Object>로 한 번에 받는다.
                             */
                            AdapterCalendar.getInstance(null).notifyItemChanged(getPosition(), this.answer);
                            AdapterCalendar.getInstance(null).notifyItemChanged(getPosition(), this.photo);
                        }
                    }, new ObjectVolley.StandardErrorListener() {
                        @Override
                        public void jobToDo() {
                            setState(LOADED_NONE);
                        }

                        @Override
                        public String tag() {
                            return "requestDiary";
                        }
                    });
        }
    }

    public int getDay() { return day; }
    public void setDay(int day) { this.day = day; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public byte[] getPhoto() { return photo; }
    public void setPhoto(byte[] photo) { this.photo = photo; }
    public int getState() { return state; }
    public void setState(int state) { this.state = state; }

    public void setPosition(int position) { this.position = position; }
    public int getPosition() { return this.position; }
}

