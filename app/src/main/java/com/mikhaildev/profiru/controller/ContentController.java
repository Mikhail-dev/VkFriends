package com.mikhaildev.profiru.controller;

import android.content.Context;

import com.mikhaildev.profiru.controller.api.ApiController;
import com.mikhaildev.profiru.model.Friend;

import java.io.IOException;
import java.util.List;


public class ContentController {

    private static ContentController instance;
    private static final Object lock = new Object();


    private ContentController() {

    }

    public static ContentController getInstance() {
        if (instance==null) {
            synchronized (lock) {
                if (instance==null)
                    instance = new ContentController();
            }
        }
        return instance;
    }

    public List<Friend> getFriends(Context context) throws IOException {
        List<Friend> friends = ApiController.getInstance().getFriends(context);
        return friends;
        //Этот класс является прослойкой для получения данных, чтобы при необходимости в нём сохранять
        // в БД данные, а не делать это в ApiController, задача которого только получить данные и их отдать
    }
}
