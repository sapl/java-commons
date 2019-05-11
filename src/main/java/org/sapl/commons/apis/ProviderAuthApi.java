package org.sapl.commons.apis;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public interface ProviderAuthApi {


    Profile getProfile(String token) throws IOException;

    String getToken(String code, String redirectUri) throws IOException;

    String buildAuthUrl(String redirectUrl, String state);

    default String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    class Profile {
        public String id;
        public String email;
        public String name;
        public String picture;

        public String getPicture() {
            return picture;
        }

        @Override public String toString() {
            return "Profile{" +
                    "email='" + email + '\'' +
                    ", name='" + name + '\'' +
                    ", picture='" + picture + '\'' +
                    '}';
        }
    }
}
