package com.example.chat;

public interface CallBackResponse {
    void onSuccess();

    void onFail(Throwable throwable);

    void onNetworkFail(Throwable throwable);
}
