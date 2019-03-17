package org.sapl.commons.utils;

import com.google.gson.Gson;
import okhttp3.*;
import okio.Buffer;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class BaseHttpClient {


    private static final int DEFAULT_TIMEOUT = 30000;

    private OkHttpClient client;
    private Gson gson = new Gson();

    public BaseHttpClient() {
        this(DEFAULT_TIMEOUT, false);
    }

    public BaseHttpClient(long timeout, boolean disableSsl) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .readTimeout(timeout, TimeUnit.MILLISECONDS);
        if (disableSsl) disableSsl(builder);
        this.client = builder.build();
    }


    public <T> T getAsObject(String url, Map<String, String> headers, Class<T> cl) throws IOException {
        return gson.fromJson(getAsString(url, headers), cl);
    }

    public String getAsString(String url, Map<String, String> headers) throws IOException {
        ResponseBody body = get(url, headers).body();
        return body != null ? body.string() : null;
    }


    public Response get(String url, Map<String, String> headers) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .headers(buildHeaders(headers))
                .get()
                .build();


        return client.newCall(request).execute();
    }


    public Response delete(String url, Map<String, String> headers) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .headers(buildHeaders(headers))
                .delete()
                .build();


        return client.newCall(request).execute();
    }

    protected String encode(String s) throws UnsupportedEncodingException {
        return URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20");
    }

    public Response post(String url, Map<String, Object> params, Map<String, String> headers)
            throws IOException {

        RequestBody formBody = getRequestBody(params);


        Request request = new Request.Builder()
                .url(url)
                .headers(buildHeaders(headers))
                .post(formBody)
                .build();

        return client.newCall(request).execute();
    }

    public Response postJson(String url, String content, Map<String, String> headers)
            throws IOException {

        RequestBody jsonBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);

        Request request = new Request.Builder()
                .url(url)
                .headers(buildHeaders(headers))
                .post(jsonBody)
                .build();

        return client.newCall(request).execute();
    }


    protected String getRequestBodyString(Map<String, Object> params) throws IOException {
        Buffer buffer = new Buffer();
        getRequestBody(params).writeTo(buffer);
        return buffer.readUtf8();
    }

    private RequestBody getRequestBody(Map<String, Object> params) {
        FormBody.Builder builder = new FormBody.Builder();

        for (String name : params.keySet())
            if (params.get(name) != null) builder.add(name, params.get(name).toString());

        return builder.build();
    }

    private Headers buildHeaders(Map<String, String> headers) {
        Headers.Builder builder = new Headers.Builder();
        if(headers!=null) for (String name : headers.keySet()) {
            builder.add(name, headers.get(name));
        }
        return builder.build();
    }

    private static OkHttpClient.Builder disableSsl(OkHttpClient.Builder builder) {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            //
        }
        return builder;
    }


}


