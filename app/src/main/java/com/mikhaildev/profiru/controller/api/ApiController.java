package com.mikhaildev.profiru.controller.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mikhaildev.profiru.R;
import com.mikhaildev.profiru.exception.ApiException;
import com.mikhaildev.profiru.exception.NetworkConnectionException;
import com.mikhaildev.profiru.model.Friend;
import com.mikhaildev.profiru.util.DateUtils;
import com.mikhaildev.profiru.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ApiController {

    private static final Object lock = new Object();
    private static ApiController instance;

    private static final String APP_URL    = "https://api.vk.com";
    private static final String URL_FRIENDS  = APP_URL + "/method/friends.get";


    public static ApiController getInstance() {
        if (instance==null) {
            synchronized (lock) {
                if (instance==null)
                    instance = new ApiController();
            }
        }
        return instance;
    }

    private ApiController() {

    }


    public List<Friend> getFriends(Context context) throws IOException {
        if (!Utils.isThereInternetConnection(context))
            throw new NetworkConnectionException();

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = openConnection(URL_FRIENDS + "?user_id=125930227&fields=photo");
            int responseCode = urlConnection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode) {
                String data = Utils.convertStreamToString(urlConnection.getInputStream());

                List<Friend> friends = new ArrayList<>(0);
                try {
                    JSONArray jsonArray = new JSONObject(data).getJSONArray("response");
                    friends = new ArrayList<>(jsonArray.length());
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            friends.add(Friend.fromJson(jsonArray.getJSONObject(i)));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //bad api. we need notify server about it
                }

                return friends;
            } else {
                throw getException(responseCode);
            }
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
    }

    public Bitmap getBigBitmapByFriendId(long friendId) throws IOException {
        String url = APP_URL + "/method/getProfiles?uids=" + friendId + "&fields=photo_big";

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = openConnection(url);
            int responseCode = urlConnection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode) {
                String data = Utils.convertStreamToString(urlConnection.getInputStream());

                try {
                    JSONArray jsonArray = new JSONObject(data).getJSONArray("response");
                    String photoFriendUrl = jsonArray.getJSONObject(0).getString("photo_big");
                    return getBitmapFromURL(photoFriendUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                    //bad api. we need notify server about it
                }

                return null;
            } else {
                throw getException(responseCode);
            }
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }

    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private HttpURLConnection openConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.addRequestProperty("Accept-Language", Locale.getDefault().getLanguage());
        urlConnection.addRequestProperty("X-Requested-With", "XMLHttpRequest");
        urlConnection.setConnectTimeout(DateUtils.SECOND * 30);
        urlConnection.setReadTimeout(DateUtils.SECOND * 60);
        return urlConnection;
    }

    private IOException getException(int responseCode) {
        return getException(null, responseCode);
    }

    private IOException getException(Exception e, int responseCode) {
        if (e!=null
                && (e.getClass().equals(java.net.UnknownHostException.class) || e.getClass().equals(java.net.SocketTimeoutException.class))) {
            return new NetworkConnectionException();
        } else if (HttpURLConnection.HTTP_INTERNAL_ERROR == responseCode) {
            return new ApiException(new Exception("Response code " + responseCode), R.string.server_error, responseCode);
        } else {
            return new ApiException(new Exception("Response code " + responseCode), R.string.error_code_frmt, responseCode);
        }
    }
}
