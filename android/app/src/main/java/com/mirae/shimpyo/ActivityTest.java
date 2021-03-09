package com.mirae.shimpyo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.auth.Session;
import com.kakao.auth.*;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.util.exception.KakaoException;


public class ActivityTest extends AppCompatActivity {
    KakaoInterface.SessionCallback sessionCallback = new KakaoInterface.SessionCallback();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // 웹서버 통신 테스트

        TextView textView = findViewById(R.id.textView);
        com.kakao.usermgmt.LoginButton buttonLogin = findViewById(R.id.buttonLogin);
        EditText editText = findViewById(R.id.editText);
        Switch switchUseCase = findViewById(R.id.switchUseCase);
        TextView textViewUseCase = findViewById(R.id.textViewUseCase);
        Button buttonToServer = findViewById(R.id.buttonToServer);

        VolleyInterface volleyInterface = VolleyInterface.getInstance(this);
        textViewUseCase.setText(volleyInterface.getHostName());

        volleyInterface.requestAccountsAll(
            response -> textView.setText("Response: " + response),
            error -> textView.setText("Response error " + error)
        );

        buttonToServer.setOnClickListener((v) -> {
            volleyInterface.requestKakaoLogin(
                editText.getText().toString(),
                new VolleyInterface.RequestLoginListener() {
                    @Override public void jobToDo() {
                        textView.setText("no : " + this.no + ", dayOfYear : " + this.dayOfYear + ", answer : " + this.answer);
                    }
                },
                error -> { textView.setText("error : " + error.toString()); }
            );
        });

        switchUseCase.setOnCheckedChangeListener((buttonView, isChecked) -> {
            volleyInterface.toggleUseCase();
            textViewUseCase.setText(volleyInterface.getHostName());
        });


        // 로그인 기능 테스트
        Session session = Session.getCurrentSession();
        session.addCallback(sessionCallback);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.open(AuthType.KAKAO_LOGIN_ALL, ActivityTest.this);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 세션 콜백 삭제
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}