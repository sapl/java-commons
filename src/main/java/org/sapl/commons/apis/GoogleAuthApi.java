package org.sapl.commons.apis;


import com.google.gson.Gson;
import okhttp3.Response;
import org.sapl.commons.utils.BaseHttpClient;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class GoogleAuthApi implements ProviderAuthApi {

    public static final String PROVIDER_NAME = "google";

    private BaseHttpClient httpClient;
    private Gson gson = new Gson();
    private String clientId, secretKey;

    public GoogleAuthApi(String clientId, String secretKey) {
        this.httpClient = new BaseHttpClient();
        this.clientId = clientId;
        this.secretKey = secretKey;
    }


    public Profile getProfile(String token) throws IOException {
        String response = httpClient.getAsString(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                Collections.singletonMap("Authorization", "Bearer " + token));
        return gson.fromJson(response, Profile.class);
    }


    public String getToken(String code, String redirectUri) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", clientId);
        params.put("client_secret", secretKey);
        params.put("redirect_uri", redirectUri);
        params.put("grant_type", "authorization_code");
        Response response = httpClient.post("https://www.googleapis.com/oauth2/v4/token", params, null);
        Map map = gson.fromJson(response.body().string(), Map.class);
        return (String) map.get("access_token");
    }

    public String buildAuthUrl(String redirectUrl, String state) {
        return "https://accounts.google.com/o/oauth2/v2/auth?" +
                "scope=" + encode("https://www.googleapis.com/auth/userinfo.email") + "&" +
                "access_type=offline&" +
                "state=" + state + "&" +
                "include_granted_scopes=true&" +
                "redirect_uri=" + encode(redirectUrl) + "&" +
                "response_type=code&" +
                "client_id=" + encode(clientId + "");
    }


}
