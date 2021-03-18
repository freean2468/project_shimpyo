package com.mirae.shimpyo.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mirae.shimpyo.R;
import com.mirae.shimpyo.helper.Diary;
import com.mirae.shimpyo.helper.Util;

import java.util.ArrayList;
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
            Log.d("debug", "clicked : " + getAdapterPosition());
            view.setBackgroundColor(Color.CYAN);
            TextView textViewAnswer = view.findViewById(R.id.textViewAnswer);
            textViewAnswer.setText("clicked!");
            ImageView imageViewPhoto = view.findViewById(R.id.imageViewPhoto);
            imageViewPhoto.setImageResource(R.mipmap.ic_launcher_round);
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
