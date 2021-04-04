package com.mirae.shimpyo.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.android.volley.VolleyError;
import com.mirae.shimpyo.R;
import com.mirae.shimpyo.activity.ActivityQA;
import com.mirae.shimpyo.object.ObjectVolley;

import java.util.Calendar;

/**
 *
 *
 * @author 송훈일(freean2468@gmail.com)
 */
public class FragmentDialogForSave extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_for_progress, null);

        TextView textViewProgress = view.findViewById(R.id.textView);
        textViewProgress.setText("일기 저장 중");
        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        /**
         *
         *
         * @author 송훈일(freean2468@gmail.com)
         */

        Fragment01 fragment01 = Fragment01.getInstance();

        ObjectVolley.getInstance(getContext()).requestAnswer(
                fragment01.getNo(),
                fragment01.getDayOfYear(),
                fragment01.getAnswer(),
                fragment01.getPhoto(),
                new ObjectVolley.RequestAnswerListener() {
                    @Override
                    public void jobToDo() {
                        progressBar.setVisibility(View.GONE);
                        textViewProgress.setText(getString(R.string.save_ok));
                        textViewProgress.setVisibility(View.VISIBLE);
                    }
                },
                new ObjectVolley.StandardErrorListener(){
                    @Override
                    public void jobToDo() {
                        progressBar.setVisibility(View.GONE);
                        textViewProgress.setText(getString(R.string.save_failure));
                        textViewProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public String tag() {
                        return "RequestAnswer";
                    }
                }
        );

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        // Create the AlertDialog object and return it
        return builder.create();
    }
}