package com.example.chat;

import java.util.List;

public interface CallBackResponseItems {
    void onSuccess(List<ItemModel> list);

    void onFail(Throwable throwable);

    void onNetworkFail(Throwable throwable);
}
