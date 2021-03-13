package com.mirae.shimpyo.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mirae.shimpyo.R;
import com.mirae.shimpyo.adapter.AdapterCalendar;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final TextView TextViewDayOfMonth;
    private AdapterCalendar.OnItemListener onItemListener;

    public CalendarViewHolder(@NonNull View itemView) {
        super(itemView);
        TextViewDayOfMonth = itemView.findViewById(R.id.TextViewCellDay);

        this.onItemListener = onItemListener;

        itemView.setOnClickListener(this);

    }//end of CalendarViewHolder

    @Override
    public void onClick(View view) {
        //CalendarAdapter.OnItemListener.onItemClick(getAdapterPosition(), (String) TextViewDayOfMonth.getText());

    }
}//end of class