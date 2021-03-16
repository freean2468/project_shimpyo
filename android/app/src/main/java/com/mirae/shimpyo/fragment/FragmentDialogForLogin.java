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

import com.mirae.shimpyo.R;
import com.mirae.shimpyo.activity.ActivityQA;
import com.mirae.shimpyo.object.ObjectVolley;

import java.util.Calendar;

public class FragmentDialogForLogin extends DialogFragment {
    private String no;

    public FragmentDialogForLogin(String no) {
        this.no = no;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_for_progress, null);

        TextView textViewProgress = view.findViewById(R.id.textView);
        textViewProgress.setText("서버 접속 중");
        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        /**
         * ActivityLogin으로부터 회원번호를 받아 웹서버에 전달, 회원 정보를 가져온다.
         * 이 과정이 성공하면 ActivityQA로 넘어가나
         * 실패하면 서버 접속 실패 메시지를 보여주고 현재 화면에 남는다.
         *
         * @author 송훈일(freean2468@gmail.com)
         */
        ObjectVolley.getInstance(getContext()).requestKakaoLogin(
                no,
                Calendar.getInstance().get(Calendar.DAY_OF_YEAR),
                new ObjectVolley.RequestLoginListener() {
                    @Override public void jobToDo() {
                        progressBar.setVisibility(View.GONE);
                        textViewProgress.setText(getString(R.string.login_ok));
                        Fragment01 fragment01 = Fragment01.getInstance();
                        fragment01.setNo(this.no);
                        fragment01.setDayOfYear(this.dayOfYear);
                        fragment01.setAnswer(this.answer);
                        fragment01.setPhoto(this.photo);

                        Intent intent = new Intent(getContext(), ActivityQA.class);
                        intent.putExtra("no", no);
                        startActivity(intent);
                    }
                },
                error -> {
                    Log.e(getString(R.string.tag_server), "RequestLogin error");
                    progressBar.setVisibility(View.GONE);
                    textViewProgress.setText(getString(R.string.login_failure));
                }
        );

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        // Create the AlertDialog object and return it
        return builder.create();
    }
}