package com.mirae.shimpyo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class ActivityMain extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textView);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        EditText editText = findViewById(R.id.editText);
        Switch switchUseCase = findViewById(R.id.switchUseCase);
        TextView textViewUseCase = findViewById(R.id.textViewUseCase);

        VolleyInterface volleyInterface = VolleyInterface.getInstance(this);
        textViewUseCase.setText(volleyInterface.getHostName());

        volleyInterface.requestAccountsAll(
            response -> textView.setText("Response: " + response),
            error -> textView.setText("Response error " + error)
        );

        buttonLogin.setOnClickListener((v) -> {
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
    }
}