package com.mirae.shimpyo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.collection.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VolleyInterface {
    private static VolleyInterface instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context ctx;
    private String hostName;
    private final String hostNameForService;
    private final String hostNameForDevelopment;

    private VolleyInterface(Context context) {
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

        hostNameForService = ctx.getString(R.string.host_name_for_service);
        hostNameForDevelopment = ctx.getString(R.string.host_name_for_development);

        hostName = hostNameForService;
    }

    public void requestKakaoLogin(String no, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        String url = hostName + ctx.getString(R.string.url_login) + "no=" + no;
        Log.d("url", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
        addToRequestQueue(request);
    }

    public void requestAccountsAll(Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        String url = hostName + ctx.getString(R.string.url_selectAll);
        JsonArrayRequest request = new JsonArrayRequest(url, listener, errorListener);
        addToRequestQueue(request);
    }

    public static synchronized VolleyInterface getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyInterface(context);
        }
        return instance;
    }

    public String getHostName() { return hostName; }

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
        protected Bitmap photo = null;

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
                    byte[] imageData = Base64.decode(response.getString("photo"), Base64.DEFAULT);
                    photo = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            jobToDo();
        }

        abstract void jobToDo();
    }
}
