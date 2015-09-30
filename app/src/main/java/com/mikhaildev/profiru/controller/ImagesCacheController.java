package com.mikhaildev.profiru.controller;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.mikhaildev.profiru.R;
import com.mikhaildev.profiru.controller.api.ApiController;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by E.Mikhail on 29.09.2015.
 */
public class ImagesCacheController {

    private static ImagesCacheController instance;
    private static final Object lock = new Object();

    private LruCache<String, Bitmap> mMemoryCache;
    private Map<ImageView, AsyncTask> asyncTasks;

    private ImagesCacheController() {
        asyncTasks = new WeakHashMap<>();
        initCache();
    }

    public static ImagesCacheController getInstance() {
        if (instance==null) {
            synchronized (lock) {
                if (instance==null)
                    instance = new ImagesCacheController();
            }
        }
        return instance;
    }

    public void loadBitmapToImageView(String url, ImageView imageView) {
        final Bitmap bitmap = getBitmapFromMemCache(url);

        //мы должны остановить прошлый AsyncTask, потому как в адаптере идёт переиспользование
        // вьюшек, а это значит что если мы будем быстро скроллить сначала вниз, а потом вверх,
        // то url будет меняться и будет заметно что предыдущая загруженная картинка будет меняться другой, для другого человека
        if (asyncTasks.containsKey(imageView)) {
            AsyncTask task = asyncTasks.get(imageView);
            if (!task.isCancelled()) task.cancel(false);
        }

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.no_person);
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
            asyncTasks.put(imageView, task);
        }
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    private void initCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 4;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<ImageView> mImageView;

        public BitmapWorkerTask(ImageView imageView) {
            mImageView = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = ApiController.getInstance().getBitmapFromURL(params[0]);
            if (bitmap!=null)
                addBitmapToMemoryCache(params[0], bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mImageView != null && bitmap != null) {
                final ImageView imageView = mImageView.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
}
