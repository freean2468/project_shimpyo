package com.mirae.shimpyo.object;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.collection.LruCache;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mirae.shimpyo.R;
import com.mirae.shimpyo.helper.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 웹서버와 통신을 담당하는 Volley Manager class
 * Singleton pattern 적용 
 *
 * @author 송훈일(freean2468@gmail.com)
 */
public class ObjectVolley {
    private static ObjectVolley instance;
    private RequestQueue requestQueue;
    private static Context ctx;
    private String hostName;
    private final String hostNameForService;
    private final String hostNameForDevelopment;

    private ObjectVolley(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();

        /** 서비스 중인 웹서버 hostname */
        hostNameForService = ctx.getString(R.string.host_name_for_service);
        /** 개발 중인 local 서버 hostname */
        hostNameForDevelopment = ctx.getString(R.string.host_name_for_development);

        hostName = hostNameForDevelopment;
    }

    public static synchronized ObjectVolley getInstance(Context context) {
        if (instance == null) {
            instance = new ObjectVolley(context);
        }
        return instance;
    }

    /**
     * @return 현재 선택된 웹서버 hostname
     */
    public String getHostName() { return hostName; }

    /**
     * 연결할 웹서버 전환
     */
    public void toggleUseCase() {
        if (hostName.equals(hostNameForService)){
            hostName = hostNameForDevelopment;
        } else {
            hostName = hostNameForService;
        }
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    private <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**
     * 카카오 로그인 후 회원번호로 다시 자체 웹서버에 회원 정보를 요청하는 함수
     * @param no 카카오 회원 번호
     * @param listener 응답 성공 시 RequestLoginLister에서 jobToDo() 함수에서 로직을 구현할 것.
     * @param errorListener 응답 실패 시 Listener
     */
    public void requestKakaoLogin(String no, int dayOfYear, RequestLoginListener listener, Response.ErrorListener errorListener) {
        String url = hostName + ctx.getString(R.string.url_login) + "no=" + no + "&d=" + dayOfYear;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
        addToRequestQueue(request);
    }

    /**
     * RequstLogin 요청에 대한 응답 wrapper abstract class
     * jobToDo 내용만 구현하고, 필드가 null인지 아닌지만 확인해서 사용하면 된다.
     */
    abstract public static class RequestLoginListener implements Response.Listener<JSONObject> {
        private String no;
        private int dayOfYear;
        private String question;
        private String answer;
        private byte[] photo = new byte[]{};

        @Override
        public void onResponse(JSONObject response) {
            try {
                no = response.getString("no");
                dayOfYear = response.getInt("dayOfYear");
                question = response.getString("question");
                answer = response.getString("answer");
                Log.d("debug", "dayOfYear : " + dayOfYear);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!response.isNull("photo")) {
                try {
                    String sPhoto = response.getString("photo");
                    if (sPhoto.length() > 0) {
//                        Log.d(ctx.getString(R.string.tag_server), "sPhoto length : " + sPhoto.length());
                        photo = Util.stringToByteArray(sPhoto);
                    }
                } catch (JSONException je) {
                    je.printStackTrace();
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                }
            }
//            Log.d("debug", "no : " + no + ", dayOfYear : " + dayOfYear + ", answer : " + answer + ", photo : " + photo);
            jobToDo();
        }

        public abstract void jobToDo();

        public String getNo() {
            return no;
        }

        public int getDayOfYear() {
            return dayOfYear;
        }

        public String getAnswer() {
            return answer;
        }

        public byte[] getPhoto() {
            return photo;
        }

        public String getQuestion() { return question; }
    }

    /**
     * 유저가 질문에 대한 답변을 작성한 후 서버에 제출할 때 호출하는 함수
     *
     * @param no 유저 회원번호
     * @param dayOfYear 365일 중 몇 번째 날?
     * @param answer 유저가 질문에 대답해 작성한 내용
     * @param photo byte[]
     * @param listener 성공 시 Listener
     * @param errorListener 실패 시 Listener
     */
    public void requestAnswer(String no, int dayOfYear, String answer, byte[] photo, RequestAnswerListener listener, Response.ErrorListener errorListener) {
        String url = hostName + ctx.getString(R.string.url_answer);
//        Map<String, String> params = new HashMap<String, String>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, listener, errorListener) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("no", no);
                params.put("d", String.valueOf(dayOfYear));
                params.put("a", answer);
                Log.d(ctx.getString(R.string.tag_server), "photo byte[] length : " + String.valueOf(photo.length));
                String sPhoto = Util.byteArrayToString(photo);
                Log.d(ctx.getString(R.string.tag_server), "photo string length : " + String.valueOf(sPhoto.length()));
                params.put("p", sPhoto);

                return params;
            }
        };
        addToRequestQueue(stringRequest);
    }

    /**
     * RequstAnswer 요청에 대한 응답 wrapper abstract class
     * jobToDo 내용만 구현하고, 필드가 null인지 아닌지만 확인해서 사용하면 된다.
     */
    abstract public static class RequestAnswerListener implements Response.Listener<String> {
        @Override
        public void onResponse(String string) {
//            Log.d(ctx.getString(R.string.tag_server), string);
            jobToDo();
        }

        public abstract void jobToDo();
    }

    public void requestQuestion(int dayOfYear, RequestQuestionListener listener, StandardErrorListener errorListener) {
        String url = hostName + ctx.getString(R.string.url_question) + "d=" + dayOfYear;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
        addToRequestQueue(request);
    }

    abstract public static class RequestQuestionListener implements Response.Listener<JSONObject> {
        private String question;

        @Override
        public void onResponse(JSONObject response) {
            try {
                question = response.getString("question");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jobToDo();
        }

        public abstract void jobToDo();
        public String getQuestion() { return question; }
    }

    /**
     * 하루 일기 정보를 서버에 요청
     *
     * @param no 유저 회원 번호
     * @param dayOfYear 정보를 요청하는 달
     * @param listener
     * @param errorListener
     */
    public void requestDiary(String no, int dayOfYear, RequestDiaryListener listener, StandardErrorListener errorListener) {
        String url = hostName + ctx.getString(R.string.url_diary) + "no=" + no + "&d=" + dayOfYear;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
        addToRequestQueue(request);
    }

    abstract public static class RequestDiaryListener implements Response.Listener<JSONObject> {
        protected String no;
        protected int dayOfYear;
        protected String answer;
        protected byte[] photo = new byte[]{};

        @Override
        public void onResponse(JSONObject response) {
            try {
                no = response.getString("no");
                dayOfYear = response.getInt("dayOfYear");
                answer = response.getString("answer");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!response.isNull("photo")) {
                try {
                    String sPhoto = response.getString("photo");
                    if (sPhoto.length() > 0) {
                        Log.d(ctx.getString(R.string.tag_server), "sPhoto length : " + sPhoto.length());
                        photo = Util.stringToByteArray(sPhoto);
                    }
                } catch (JSONException je) {
                    je.printStackTrace();
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                }
            }
//            Log.d(ctx.getString(R.string.tag_server), "RequestDiaryListener 응답 성공!");
            jobToDo();
        }

        public abstract void jobToDo();
    }

    /**
     *  에러 시 서버 응답 코드를 자동으로 알려주는 class
     *
     * @author 송훈일(freean2468@gmail.com)
     */
    abstract public static class StandardErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i(ctx.getString(R.string.tag_server), tag() + " : " + error.toString() + ", STATUS_CODE : " + volleyResponseStatusCode(error));
            jobToDo();
        }

        public static int volleyResponseStatusCode(VolleyError error)
        {
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null) {
                return networkResponse.statusCode;
            }
            else{
                return 0;
            }
        }

        public abstract void jobToDo();
        public abstract String tag();
    }
}
