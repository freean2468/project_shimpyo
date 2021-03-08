package com.mirae.shimpyo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

        // bridged IP
        String url = "http://shimpyo-env.eba-gxbgkhwf.ap-northeast-2.elasticbeanstalk.com/db/selectAll";
        VolleyInterface volleyInterface = VolleyInterface.getInstance(getApplicationContext());

        JsonArrayRequest request = new JsonArrayRequest(
                url,
                response -> textView.setText("Response: " + response),
                error -> textView.setText("Response error " + error));

        volleyInterface.addToRequestQueue(request);
    }
}