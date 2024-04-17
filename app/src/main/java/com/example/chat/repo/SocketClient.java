package com.example.chat.repo;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;

import com.example.chat.App;
import com.example.chat.HomeFragment;
import com.example.chat.MessageModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketClient {


    public interface CallBack {
        void onMessage(String message);
    }

    public Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private final List<CallBack> callBacks;
    private final Handler uiHandler;
    public String email;
    public String ip;

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

    public void sendImage(String from, String to, byte[] bytes) {
        executorService.execute(() -> {
            int size = bytes.length / 4096;
            long id = System.currentTimeMillis();
            if (size * 4096 > bytes.length)
                size++;
            for (int i = 0; i < size; i++) {
                byte[] newBytes;
                String step;
                if (i == size - 1) {
                    step = "end";
                    newBytes = Arrays.copyOfRange(bytes, i * 4096, bytes.length);
                } else {
                    step = String.valueOf(i + 1);
                    newBytes = Arrays.copyOfRange(bytes, i * 4096, (i * 4096) + 4096);
                }
                String base64 = Base64.encodeToString(newBytes, Base64.DEFAULT);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", from);
                    jsonObject.put("to", to);
                    jsonObject.put("content", base64);
                    jsonObject.put("step", step);
                    jsonObject.put("id", String.valueOf(id));
                    jsonObject.put("type", "image");
                    sendMessage(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void sendMessage(MessageModel messageModel) {
        executorService.execute(() -> {
            if (dataOutputStream != null)
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", messageModel.getName());
                    jsonObject.put("to", messageModel.getTo());
                    jsonObject.put("content", messageModel.getContent());
                    jsonObject.put("type", "message");
                    dataOutputStream.writeUTF(jsonObject.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        });

    }

    public void stop() {
        executorService.execute(() -> {
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
        });

    }
}
