package com.mirae.shimpyo.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.mirae.shimpyo.R;
import com.mirae.shimpyo.activity.ActivityQA;
import com.mirae.shimpyo.fragment.Fragment01;
import com.mirae.shimpyo.fragment.Fragment03Ver2;
import com.mirae.shimpyo.helper.Diary;
import com.mirae.shimpyo.helper.Util;
import com.mirae.shimpyo.object.ObjectVolley;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdapterCalendar extends RecyclerView.Adapter<AdapterCalendar.HolderViewCalendar> {
    private static ArrayList<Diary> daysOfMonth;
    private static AdapterCalendar instance;

    private AdapterCalendar() {}

    public static AdapterCalendar getInstance(ArrayList<Diary> textViewDaysOfMonth) {
        if (instance == null) {
            daysOfMonth = textViewDaysOfMonth;
            instance = new AdapterCalendar();
        }
        return instance;
    }

    public static ArrayList<Diary> getDaysOfMonth() { return daysOfMonth; }

    @NonNull
    @Override
    public HolderViewCalendar onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);

        return new HolderViewCalendar(view);
    }

    /**
     * Update only part of ViewHolder that you are interested in
     * Invoked before onBindViewHolder(ViewHolder holder, int position)
     *
     * onBindViewHolder(ViewHolder holder, int position) 후에 특정한 요소가 갱신될 때마다
     * 데이터를 갱신한 후 필요한 view를 갱신하는 부분
     *
     * @param holder
     * @param position
     * @param payloads NotifyItemChanged 함수를 통해 전달된 인자들
     */
    @Override
    public void onBindViewHolder(HolderViewCalendar holder, int position, List<Object> payloads) {
        if(!payloads.isEmpty()) {
            if (payloads.get(0) instanceof String) {
                holder.getTextViewAnswer().setText(String.valueOf((String)payloads.get(0)));
            }
            if (payloads.get(1) instanceof byte[]) {
                holder.getImageViewPhoto().setImageBitmap(Util.byteArrayToBitmap((byte[])payloads.get(1)));
            }
        } else {
            super.onBindViewHolder(holder,position, payloads);
        }
    }

    /**
     * 기존 view 디자인에 가장 기초적으로 뿌려져야 할 것들은 여기서 갱신
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull HolderViewCalendar holder, int position) {
        if (daysOfMonth.get(position).getState() == Diary.NOT_EXISTS) {
            holder.itemView.setVisibility(View.GONE);
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.getTextViewCellDay().setText(String.valueOf(daysOfMonth.get(position).getDay()));

            byte[] photo = daysOfMonth.get(position).getPhoto();

            if (photo != null && photo.length > 0){
                Log.d("debug", "photo.length : " + photo.length);

                holder.getImageViewPhoto().setImageBitmap(Util.byteArrayToBitmap(photo));
            }
        }
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public class HolderViewCalendar extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textViewCellDay;
        private TextView textViewAnswer;
        private ImageView imageViewPhoto;

        public HolderViewCalendar(@NonNull View itemView) {
            super(itemView);
            textViewCellDay = itemView.findViewById(R.id.textViewCellDay);
            textViewAnswer = itemView.findViewById(R.id.textViewAnswer);
            imageViewPhoto = itemView.findViewById(R.id.imageViewPhoto);

            itemView.setOnClickListener(this);
        }//end of CalendarViewHolder

        @Override
        public void onClick(View view) {
            Fragment03Ver2 fragment03Ver2 = Fragment03Ver2.getInstance();
            LocalDate selectedDate = fragment03Ver2.getLocalDateSelect();

            int month = selectedDate.getMonthValue();
            int day = Integer.parseInt(getTextViewCellDay().getText().toString());
            Calendar c = Calendar.getInstance();
            c.set(LocalDate.now().getYear(), month - 1, day, 0, 0);
            int dayOfYear = c.get(Calendar.DAY_OF_YEAR);
            Fragment01 fragment01 = Fragment01.getInstance();
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) getImageViewPhoto().getDrawable());

            ObjectVolley objectVolley = ObjectVolley.getInstance(view.getContext());
            objectVolley.requestQuestion(
                    dayOfYear,
                    new ObjectVolley.RequestQuestionListener() {
                        @Override
                        public void jobToDo() {
                            if (bitmapDrawable == null) {
                                fragment01.setPhoto();
                            } else {
                                Bitmap bitmap = bitmapDrawable.getBitmap();
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] imageInByte = baos.toByteArray();
                                fragment01.setPhoto(imageInByte);
                                try {
                                    baos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            fragment01.setDayOfYear(dayOfYear);
                            fragment01.setQuestion(getQuestion());
                            fragment01.setAnswer(getTextViewAnswer().getText().toString());
                            fragment01.setAnswerView();
                            ActivityQA.viewPager.setCurrentItem(AdapterViewPager.FRAGMENT_01);
                        }
                    },
                    new ObjectVolley.StandardErrorListener() {
                        @Override
                        public void jobToDo() {

                        }

                        @Override
                        public String tag() {
                            return null;
                        }
                    }
            );
        }

        public TextView getTextViewCellDay() {
            return textViewCellDay;
        }

        public TextView getTextViewAnswer() {
            return textViewAnswer;
        }

        public ImageView getImageViewPhoto() {
            return imageViewPhoto;
        }

        public void setTextViewAnswer(TextView textViewAnswer) {
            this.textViewAnswer = textViewAnswer;
        }

        public void setImageViewPhoto(ImageView imageViewPhoto) {
            this.imageViewPhoto = imageViewPhoto;
        }

        public void setTextViewCellDay(TextView textViewCellDay) {
            if (this.textViewCellDay != null)
                this.textViewCellDay = textViewCellDay;
        }
    }//end of class
}
