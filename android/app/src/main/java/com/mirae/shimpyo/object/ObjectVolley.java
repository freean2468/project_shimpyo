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
                        Log.d(ctx.getString(R.string.tag_server), "photo string : " + String.valueOf(photo.length));
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
        Map<String, String> params = new HashMap<String, String>();
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
            Log.d(ctx.getString(R.string.tag_server), string);
            jobToDo();
        }

        public abstract void jobToDo();
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
        String url = hostName + "service/diary/1?" + "no=" + no + "&d=" + dayOfYear;
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
                        Log.d(ctx.getString(R.string.tag_server), "photo string : " + String.valueOf(photo.length));
                        photo = Util.stringToByteArray(sPhoto);
                    }
                } catch (JSONException je) {
                    je.printStackTrace();
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                }
            }
            Log.d(ctx.getString(R.string.tag_server), "RequestDiaryListener 응답 성공!");
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

    /**
     * volley에서 공식적으로 file 전송 multi-param을 지원하지 않아
     * 만들어진 커스텀 file 전송용 request
     *
     * #author https://www.maxester.com/blog/2019/10/04/upload-file-image-to-the-server-using-volley-in-android/
     */
    public class VolleyMultipartRequest extends Request<NetworkResponse> {
        private final String twoHyphens = "--";
        private final String lineEnd = "\r\n";
        private final String boundary = "apiclient-" + System.currentTimeMillis();

        private Response.Listener<NetworkResponse> mListener;
        private Response.ErrorListener mErrorListener;
        private Map<String, String> mHeaders;


        public VolleyMultipartRequest(int method, String url,
                                      Response.Listener<NetworkResponse> listener,
                                      Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            this.mListener = listener;
            this.mErrorListener = errorListener;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return (mHeaders != null) ? mHeaders : super.getHeaders();
        }

        @Override
        public String getBodyContentType() {
            return "multipart/form-data;boundary=" + boundary;
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);

            try {
                // populate text payload
                Map<String, String> params = getParams();
                if (params != null && params.size() > 0) {
                    textParse(dos, params, getParamsEncoding());
                }

                // populate data byte payload
                Map<String, DataPart> data = getByteData();
                if (data != null && data.size() > 0) {
                    dataParse(dos, data);
                }

                // close multipart form data after text and file data
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                return bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Custom method handle data payload.
         *
         * @return Map data part label with data byte
         * @throws AuthFailureError
         */
        protected Map<String, DataPart> getByteData() throws AuthFailureError {
            return null;
        }

        @Override
        protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
            try {
                return Response.success(
                        response,
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (Exception e) {
                return Response.error(new ParseError(e));
            }
        }

        @Override
        protected void deliverResponse(NetworkResponse response) {
            mListener.onResponse(response);
        }

        @Override
        public void deliverError(VolleyError error) {
            mErrorListener.onErrorResponse(error);
        }

        /**
         * Parse string map into data output stream by key and value.
         *
         * @param dataOutputStream data output stream handle string parsing
         * @param params           string inputs collection
         * @param encoding         encode the inputs, default UTF-8
         * @throws IOException
         */
        private void textParse(DataOutputStream dataOutputStream, Map<String, String> params, String encoding) throws IOException {
            try {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    buildTextPart(dataOutputStream, entry.getKey(), entry.getValue());
                }
            } catch (UnsupportedEncodingException uee) {
                throw new RuntimeException("Encoding not supported: " + encoding, uee);
            }
        }

        /**
         * Parse data into data output stream.
         *
         * @param dataOutputStream data output stream handle file attachment
         * @param data             loop through data
         * @throws IOException
         */
        private void dataParse(DataOutputStream dataOutputStream, Map<String, DataPart> data) throws IOException {
            for (Map.Entry<String, DataPart> entry : data.entrySet()) {
                buildDataPart(dataOutputStream, entry.getValue(), entry.getKey());
            }
        }

        /**
         * Write string data into header and data output stream.
         *
         * @param dataOutputStream data output stream handle string parsing
         * @param parameterName    name of input
         * @param parameterValue   value of input
         * @throws IOException
         */
        private void buildTextPart(DataOutputStream dataOutputStream, String parameterName, String parameterValue) throws IOException {
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes(parameterValue + lineEnd);
        }

        /**
         * Write data file into header and data output stream.
         *
         * @param dataOutputStream data output stream handle data parsing
         * @param dataFile         data byte as DataPart from collection
         * @param inputName        name of data input
         * @throws IOException
         */
        private void buildDataPart(DataOutputStream dataOutputStream, DataPart dataFile, String inputName) throws IOException {
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                    inputName + "\"; filename=\"" + dataFile.getFileName() + "\"" + lineEnd);
            if (dataFile.getType() != null && !dataFile.getType().trim().isEmpty()) {
                dataOutputStream.writeBytes("Content-Type: " + dataFile.getType() + lineEnd);
            }
            dataOutputStream.writeBytes(lineEnd);

            ByteArrayInputStream fileInputStream = new ByteArrayInputStream(dataFile.getContent());
            int bytesAvailable = fileInputStream.available();

            int maxBufferSize = 1024 * 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dataOutputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dataOutputStream.writeBytes(lineEnd);
        }

        class DataPart {
            private String fileName;
            private byte[] content;
            private String type;

            public DataPart() {
            }

            DataPart(String name, byte[] data) {
                fileName = name;
                content = data;
            }

            String getFileName() {
                return fileName;
            }

            byte[] getContent() {
                return content;
            }

            String getType() {
                return type;
            }

        }
    }
}
