package com.mirae.shimpyo.holder;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mirae.shimpyo.R;
import com.mirae.shimpyo.adapter.AdapterCalendar;

public class HolderViewCalendar extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final TextView TextViewDayOfMonth;

    public HolderViewCalendar(@NonNull View itemView) {
        super(itemView);
        TextViewDayOfMonth = itemView.findViewById(R.id.TextViewCellDay);

        itemView.setOnClickListener(this);
    }//end of CalendarViewHolder

    @Override
    public void onClick(View view) {
        Log.d("debug", "clicked : " + getAdapterPosition());
    }
}//end of class
