package com.mirae.shimpyo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

        VolleyInterface volleyInterface = VolleyInterface.getInstance(this);

        volleyInterface.allAccounts(
            response -> textView.setText("Response: " + response),
            error -> textView.setText("Response error " + error)
        );

        buttonLogin.setOnClickListener((v) -> {
            volleyInterface.kakaoLogin(
                editText.getText().toString(),
                response -> { textView.setText("response : " + response.toString()); },
                error -> { textView.setText("error : " + error.toString()); }
            );
        });
    }
}