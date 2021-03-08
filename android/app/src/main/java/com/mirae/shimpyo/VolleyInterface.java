package com.mirae.shimpyo;

import android.content.Context;
import android.graphics.Bitmap;
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
import org.json.JSONObject;

public class VolleyInterface {
    private static VolleyInterface instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context ctx;
    private final String hostName;

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

        hostName = ctx.getString(R.string.host_name);
    }

    public void kakaoLogin(String no, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        String url = hostName + ctx.getString(R.string.url_login) + "no=" + no;
        Log.d("url", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
        addToRequestQueue(request);
    }

    public void allAccounts(Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
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
}
