package com.example.chat.repo;

import android.content.Context;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.example.chat.CallBackResponseBitmap;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoadImage {

    private static LoadImage image;

    private OkHttpClient loadImage;

    public LoadImage(Context context) {


        File cacheFolder = new File(context.getCacheDir(), "images");
        if (!cacheFolder.exists())
            cacheFolder.mkdirs();
        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(cacheFolder, cacheSize);

        loadImage = new OkHttpClient.Builder()
                .cache(cache)
                .build();

    }
    public static LoadImage getImage(Context context) {
        if (image == null)
            image = new LoadImage(context);
        return image;
    }

    public void getBitmap(String url, CallBackResponseBitmap callBackResponseBitmap) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        loadImage.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (callBackResponseBitmap != null)
                    callBackResponseBitmap.onNetworkFail(e);

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (callBackResponseBitmap != null)
                    if (response.isSuccessful())
                        callBackResponseBitmap.onSuccess(BitmapFactory.decodeStream(response.body().byteStream()));
                    else
                        callBackResponseBitmap.onFail(new Throwable(response.body().string()));
            }
        });
    }
}
