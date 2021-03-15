package com.mirae.shimpyo.holder;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mirae.shimpyo.R;
import com.mirae.shimpyo.adapter.AdapterCalendar;

public class HolderViewCalendar extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final TextView textViewDayOfMonth;

    public HolderViewCalendar(@NonNull View itemView) {
        super(itemView);
        textViewDayOfMonth = itemView.findViewById(R.id.textViewCellDay);

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
}//end of class
