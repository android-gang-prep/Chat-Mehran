package com.example.chat;

import android.graphics.Bitmap;

public interface CallBackResponseBitmap {
    void onSuccess(Bitmap bitmap);

    void onFail(Throwable throwable);

    void onNetworkFail(Throwable throwable);
}
