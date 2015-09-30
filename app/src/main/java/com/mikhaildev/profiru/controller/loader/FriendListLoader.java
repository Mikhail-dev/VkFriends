package com.mikhaildev.profiru.controller.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.mikhaildev.profiru.controller.ContentController;
import com.mikhaildev.profiru.model.AsyncResult;
import com.mikhaildev.profiru.model.Friend;

import java.util.List;


public class FriendListLoader extends AsyncTaskLoader<AsyncResult<List<Friend>>> {

    private AsyncResult<List<Friend>> mResult;


    public FriendListLoader(Context context) {
        super(context);
    }

    @Override
    public AsyncResult<List<Friend>> loadInBackground() {
        AsyncResult<List<Friend>> data = new AsyncResult<>();
        try {
            data.setData(ContentController.getInstance().getFriends(getContext()));
        } catch (Exception e) {
            data.setException(e);
        }
        return data;
    }

    @Override
    public void deliverResult(AsyncResult<List<Friend>> result) {
        mResult = result;
        if (isStarted()) {
            super.deliverResult(result);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mResult != null) {
            deliverResult(mResult);
        }

        if (takeContentChanged() || mResult == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        mResult = null;
    }
}
