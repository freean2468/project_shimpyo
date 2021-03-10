package com.mirae.shimpyo.object;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.collection.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mirae.shimpyo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 웹서버와 통신을 담당하는 Volley Manager class
 * Singleton pattern 적
 *
 * @author 송훈일(freean2468@gmail.com)
 */
public class ObjectVolley {
    private static ObjectVolley instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context ctx;
    private String hostName;
    private final String hostNameForService;
    private final String hostNameForDevelopment;

    private ObjectVolley(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue,
            new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap>
                        cache = new LruCache<String, Bitmap>(20);

                @Override
                public Bitmap getBitmap(String url) {
                    return cache.get(url);
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                    cache.put(url, bitmap);
                }
            });

        /** 서비스 중인 웹서버 hostname */
        hostNameForService = ctx.getString(R.string.host_name_for_service);
        /** 개발 중인 local 서버 hostname */
        hostNameForDevelopment = ctx.getString(R.string.host_name_for_development);

        hostName = hostNameForService;
    }

    /**
     * 카카오 로그인 후 회원번호로 다시 자체 웹서버에 회원 정보를 요청하는 함수
     * @param no 카카오 회원 번호
     * @param listener 응답 성공 시 RequestLoginLister에서 jobToDo() 함수에서 로직을 구현할 것.
     * @param errorListener 응답 실패 시 Listener
     */
    public void requestKakaoLogin(String no, RequestLoginListener listener, Response.ErrorListener errorListener) {
        String url = hostName + ctx.getString(R.string.url_login) + "no=" + no;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
        addToRequestQueue(request);
    }

    /**
     * 모든 회원 정보를 요청하는 테스트용 함수
     * 서비스에는 부적합한 함수.
     *
     * @todo 없애자 이 함수.
     * @param listener
     * @param errorListener
     */
    public void requestAccountsAll(Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        String url = hostName + ctx.getString(R.string.url_selectAll);
        JsonArrayRequest request = new JsonArrayRequest(url, listener, errorListener);
        addToRequestQueue(request);
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

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    abstract public static class RequestLoginListener implements Response.Listener<JSONObject> {
        protected String no;
        protected int dayOfYear;
        protected String answer;
        protected byte[] photo = null;

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
                    photo = Base64.decode(response.getString("photo"), Base64.DEFAULT);
//                    photo = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            jobToDo();
        }

        public abstract void jobToDo();
    }
}
