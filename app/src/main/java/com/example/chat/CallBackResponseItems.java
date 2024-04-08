package com.example.chat;

import java.util.List;

public interface CallBackResponseItems<T> {
    void onSuccess(T list);

    void onFail(Throwable throwable);

    void onNetworkFail(Throwable throwable);
}
