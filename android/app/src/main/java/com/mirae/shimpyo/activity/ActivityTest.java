package com.mirae.shimpyo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.mirae.shimpyo.object.ObjectVolley;
import com.mirae.shimpyo.R;

/**
 * 테스트를 위한 모든 기능 구현 부분을 통합해 놓은 테스트용 액티비티
 *
 * @author 송훈일(freean2468@gmail.com)
 */
public class ActivityTest extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        /**
         * 웹서버 통신 테스트
         */

        TextView textView = findViewById(R.id.textView);
        EditText editText = findViewById(R.id.editText);
        Switch switchUseCase = findViewById(R.id.switchUseCase);
        TextView textViewUseCase = findViewById(R.id.textViewUseCase);
        Button buttonToServer = findViewById(R.id.buttonToServer);

        ObjectVolley objectVolley = ObjectVolley.getInstance(this);
        textViewUseCase.setText(objectVolley.getHostName());

        objectVolley.requestAccountsAll(
            response -> textView.setText("Response: " + response),
            error -> textView.setText("Response error " + error)
        );

        buttonToServer.setOnClickListener((v) -> {
            objectVolley.requestKakaoLogin(
                editText.getText().toString(),
                new ObjectVolley.RequestLoginListener() {
                    @Override public void jobToDo() {
                        textView.setText("no : " + this.no + ", dayOfYear : " + this.dayOfYear + ", answer : " + this.answer);
                    }
                },
                error -> { textView.setText("error : " + error.toString()); }
            );
        });

        switchUseCase.setOnCheckedChangeListener((buttonView, isChecked) -> {
            objectVolley.toggleUseCase();
            textViewUseCase.setText(objectVolley.getHostName());
        });
    }
}