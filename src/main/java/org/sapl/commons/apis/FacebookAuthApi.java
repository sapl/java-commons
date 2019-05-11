package org.sapl.commons.apis;


import com.google.gson.Gson;
import org.sapl.commons.utils.BaseHttpClient;

import java.io.IOException;
import java.util.Map;


public class FacebookAuthApi implements ProviderAuthApi {

    public static final String PROVIDER_NAME = "facebook";

    private BaseHttpClient httpClient;
    private Gson gson = new Gson();
    private String clientId, secretKey;

    public FacebookAuthApi(String clientId, String secretKey) {
        this.httpClient = new BaseHttpClient();
        this.clientId = clientId;
        this.secretKey = secretKey;
    }


    public Profile getProfile(String token) throws IOException {
        String response = httpClient.getAsString(
                "https://graph.facebook.com/me?fields=name,email&access_token=" + token, null);
        Profile profile = gson.fromJson(response, Profile.class);
        String url = "https://graph.facebook.com/me/picture?type=normal&access_token=" + token;
//        profile.picture = HttpUtil.getLocation(url, httpClient);
        return profile;
    }


    public String getToken(String code, String redirectUri) throws IOException {

        String response = httpClient.getAsString("" +
                "https://graph.facebook.com/v2.9/oauth/access_token?" +
                "client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&client_secret=" + secretKey +
                "&code=" + code, null);
        Map map = gson.fromJson(response, Map.class);
        return (String) map.get("access_token");
    }

    public String buildAuthUrl(String redirectUrl, String state) {
        return "https://www.facebook.com/v2.9/dialog/oauth?" +
                "client_id=" + clientId +
                "&redirect_uri=" + encode(redirectUrl) +
                "&response_type=code" +
                "&scope=email" +
                "&state=" + state;
    }


}
