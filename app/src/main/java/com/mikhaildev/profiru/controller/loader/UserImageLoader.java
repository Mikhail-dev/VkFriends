package com.mikhaildev.profiru.controller.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.AsyncTaskLoader;

import com.mikhaildev.profiru.controller.api.ApiController;
import com.mikhaildev.profiru.model.AsyncResult;

/**
 * Created by E.Mikhail on 29.09.2015.
 */
public class UserImageLoader extends AsyncTaskLoader<AsyncResult<Bitmap>> {

    private AsyncResult<Bitmap> mResult;
    private long mFriendId;

    public UserImageLoader(Context context, long friendId) {
        super(context);
        mFriendId = friendId;
    }

    @Override
    public AsyncResult<Bitmap> loadInBackground() {
        AsyncResult<Bitmap> data = new AsyncResult<>();
        try {
            data.setData(ApiController.getInstance().getBigBitmapByFriendId(mFriendId));
        } catch (Exception e) {
            data.setException(e);
        }
        return data;
    }

    @Override
    public void deliverResult(AsyncResult<Bitmap> result) {
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
