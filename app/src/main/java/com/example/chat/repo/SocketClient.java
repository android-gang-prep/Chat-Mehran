package com.example.chat.repo;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketClient {


    public interface CallBack {
        void onMessage(String message);
    }

    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private final List<CallBack> callBacks;
    private final Handler uiHandler;
    private String email;
    private String ip;

    private ExecutorService executorService;

    public SocketClient() {
        callBacks = new ArrayList<>();
        this.executorService = Executors.newCachedThreadPool();
        this.uiHandler = new Handler(Looper.getMainLooper());
    }

    public void addListener(CallBack callBack) {
        if (callBacks.contains(callBack))
            return;

        callBacks.add(callBack);
    }

    public void removeListener(CallBack callBack) {
        callBacks.remove(callBack);
    }

    public void start(String ip, String email) {
        this.email = email;
        if (socket != null) {
            if (!this.ip.equals(ip)) {
                stop();
                start(ip, email);
            }
            return;
        }
        this.ip = ip;
        executorService.execute(() -> {
            try {
                socket = new Socket(ip, 6985);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                try {
                    setOnline();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                while (true) {
                    String message = dataInputStream.readUTF();
                    if (message != null && !message.isEmpty()) {
                        for (int i = 0; i < callBacks.size(); i++) {
                            callBacks.get(i).onMessage(message);
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                    socket = null;
                    dataOutputStream = null;
                    dataInputStream = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    Thread.sleep(2000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                start(ip, email);
            }
        });
    }

    public void setOnline() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("content", "1");
        jsonObject.put("name", email);
        jsonObject.put("type", "online");
        sendMessage(jsonObject.toString());
    }

    public void setOffline() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("content", "0");
        jsonObject.put("name", email);
        jsonObject.put("type", "online");
        sendMessage(jsonObject.toString());
    }

    public void sendMessage(String message) {
        executorService.execute(() -> {
            if (dataOutputStream != null)
                try {
                    dataOutputStream.writeUTF(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        });

    }

    public void stop() {
     /*   executorService.execute(() -> {
            try {
                if (socket == null)
                    return;
                socket.close();
                socket = null;
                dataOutputStream = null;
                dataInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            executorService.shutdown();
            executorService = Executors.newCachedThreadPool();

        });*/

    }
}
