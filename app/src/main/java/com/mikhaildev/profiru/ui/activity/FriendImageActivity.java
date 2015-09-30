package com.mikhaildev.profiru.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ImageView;

import com.mikhaildev.profiru.R;
import com.mikhaildev.profiru.controller.ImagesCacheController;
import com.mikhaildev.profiru.controller.loader.UserImageLoader;
import com.mikhaildev.profiru.model.AsyncResult;
import com.mikhaildev.profiru.model.Friend;
import com.mikhaildev.profiru.util.StringUtils;
import com.mikhaildev.profiru.util.UiUtils;

/**
 * Created by E.Mikhail on 30.09.2015.
 */
public class FriendImageActivity extends BaseActivity
        implements
            LoaderManager.LoaderCallbacks,
            View.OnClickListener {

    private final int LOADER_ID_LOAD_FRIEND_IMAGE = 2;

    private Friend friend;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friend = (Friend) getIntent().getSerializableExtra(StringUtils.EXTRA_DATA);
        imageView = (ImageView) findViewById(R.id.photo);
        imageView.setOnClickListener(this);
        ImagesCacheController.getInstance().loadBitmapToImageView(friend.getPhotoUrl(), imageView);
        getSupportLoaderManager().initLoader(LOADER_ID_LOAD_FRIEND_IMAGE, null, this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.a_friend_image;
    }

    @Override
    public Loader onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case LOADER_ID_LOAD_FRIEND_IMAGE:
                return new UserImageLoader(this, friend.getUid());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case LOADER_ID_LOAD_FRIEND_IMAGE:
                handleFriendImageResponse(data);
                break;
        }
    }

    private void handleFriendImageResponse(Object data) {
        AsyncResult<Bitmap> result = (AsyncResult<Bitmap>) data;
        if (result.getData() != null) {
            Bitmap bitmap = result.getData();
            imageView.setImageBitmap(bitmap);
        } else {
            UiUtils.showException(this, result.getException());
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo:
                onBackPressed();
                break;
        }
    }
}
