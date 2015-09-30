package com.mikhaildev.profiru.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by E.Mikhail on 29.09.2015.
 */
public class Friend implements Serializable {

    public static final String UID        = "uid";
    public static final String USER_ID    = "user_id";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME  = "last_name";
    public static final String PHOTO_URL  = "photo";

    private long uid;
    private long userId;
    private String firstName;
    private String lastName;
    private String photoUrl;

    public long getUid() {
        return uid;
    }

    public long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public static Friend fromJson(JSONObject jsonString) throws JSONException {
        Friend newFriend = new Friend();

        newFriend.uid = Long.parseLong(jsonString.getString(UID));
        newFriend.userId = Long.parseLong(jsonString.getString(USER_ID));
        newFriend.firstName = jsonString.getString(FIRST_NAME);
        newFriend.lastName = jsonString.getString(LAST_NAME);
        newFriend.photoUrl = jsonString.getString(PHOTO_URL);

        return newFriend;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "uid=" + uid +
                ", userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}