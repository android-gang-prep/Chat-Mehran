package com.example.chat;

import android.app.Application;

import com.example.chat.repo.SocketClient;

public class App extends Application {
    public SocketClient socketClient;

    private static App app;

    public static App getApp() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        socketClient = new SocketClient();

    }
}
