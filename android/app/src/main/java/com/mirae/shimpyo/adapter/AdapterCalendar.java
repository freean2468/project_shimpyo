package com.mirae.shimpyo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mirae.shimpyo.R;
import com.mirae.shimpyo.holder.HolderViewCalendar;

import java.util.ArrayList;

public class AdapterCalendar extends RecyclerView.Adapter<HolderViewCalendar> {
    private final ArrayList<String> daysOfMonth;

    public AdapterCalendar(ArrayList<String> textViewDaysOfMonth) {
        daysOfMonth = textViewDaysOfMonth;
    }

    @NonNull
    @Override
    public HolderViewCalendar onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);

        return new HolderViewCalendar(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderViewCalendar holder, int position) {
        holder.textViewDayOfMonth.setText(daysOfMonth.get(position));
        if (holder.textViewDayOfMonth.getText().toString().equals(""))
            holder.itemView.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }
}
