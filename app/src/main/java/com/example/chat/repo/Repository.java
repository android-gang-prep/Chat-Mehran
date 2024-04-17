package com.example.chat.repo;

import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.example.chat.CallBackResponse;
import com.example.chat.CallBackResponseBitmap;
import com.example.chat.CallBackResponseItems;
import com.example.chat.ItemModel;
import com.example.chat.ListItemModel;
import com.example.chat.ListLiveModel;
import com.example.chat.LiveModel;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Repository {

    private OkHttpClient client;

    private static Repository repository;

    private Repository() {
        client = new OkHttpClient();
    }

    public static Repository getRepository() {
        if (repository == null)
            repository = new Repository();
        return repository;
    }

    public void Signup(String email, String nickName, String password, String phone, CallBackResponse callBackResponse) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("nickName", nickName);
            jsonObject.put("password", password);
            jsonObject.put("phone", phone);
            post("http://wsk2019.mad.hakta.pro/api/users", jsonObject.toString(), callBackResponse);
        } catch (JSONException e) {
            callBackResponse.onNetworkFail(new Throwable("Json Error"));
        }
    }

    public void getLives(CallBackResponseItems<List<LiveModel>> callBackResponse) {

        Request request = new Request.Builder()
                .url("https://test-setare.s3.ir-tbz-sh1.arvanstorage.ir/profile_lives2.json?versionId=")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (callBackResponse != null)
                    callBackResponse.onNetworkFail(e);

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (callBackResponse != null)
                    if (response.isSuccessful()) {
                        ListLiveModel listLiveModel = new Gson().fromJson(response.body().string(), ListLiveModel.class);
                        callBackResponse.onSuccess(listLiveModel.getLives());
                    } else
                        callBackResponse.onFail(new Throwable(response.body().string()));
            }
        });
    }

    public void getItems(CallBackResponseItems<List<ItemModel>> callBackResponse) {

        Request request = new Request.Builder()
                .url("https://test-setare.s3.ir-tbz-sh1.arvanstorage.ir/wsi-lyon%2Ffavourites_avatars1.json?versionId=")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (callBackResponse != null)
                    callBackResponse.onNetworkFail(e);

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (callBackResponse != null)
                    if (response.isSuccessful()) {
                        ListItemModel listItemModel = new Gson().fromJson(response.body().string(), ListItemModel.class);

                        callBackResponse.onSuccess(listItemModel.getFavourites());
                    } else
                        callBackResponse.onFail(new Throwable(response.body().string()));
            }
        });
    }

    public void Login(String email, String password, CallBackResponse callBackResponse) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            post("http://wsk2019.mad.hakta.pro/api/user/login", jsonObject.toString(), callBackResponse);
        } catch (JSONException e) {
            callBackResponse.onNetworkFail(new Throwable("Json Error"));
        }
    }

    public void sendCode(String countryCode, String phone, CallBackResponse callBackResponse) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone", phone);
            jsonObject.put("countryCode", countryCode);
            post("http://wsk2019.mad.hakta.pro/api/user/smsCode", jsonObject.toString(), callBackResponse);
        } catch (JSONException e) {
            if (callBackResponse != null)
                callBackResponse.onNetworkFail(new Throwable("Json Error"));
        }
    }

    public void activeCode(String code, CallBackResponse callBackResponse) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", code);
            put("http://wsk2019.mad.hakta.pro/api/user/activation", jsonObject.toString(), callBackResponse);
        } catch (JSONException e) {
            if (callBackResponse != null)
                callBackResponse.onNetworkFail(new Throwable("Json Error"));
        }
    }

    public void get(String url, CallBackResponse callBackResponse) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (callBackResponse != null)
                    callBackResponse.onNetworkFail(e);

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (callBackResponse != null)
                    if (response.isSuccessful())
                        callBackResponse.onSuccess();
                    else
                        callBackResponse.onFail(new Throwable(response.body().string()));
            }
        });
    }

    public void put(String url, String json, CallBackResponse callBackResponse) {
        MediaType JSON = MediaType.get("application/json");
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (callBackResponse != null)
                    callBackResponse.onNetworkFail(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (callBackResponse != null)
                    if (response.isSuccessful())
                        callBackResponse.onSuccess();
                    else
                        callBackResponse.onFail(new Throwable(response.body().string()));
            }
        });
    }

    public void post(String url, String json, CallBackResponse callBackResponse) {
        MediaType JSON = MediaType.get("application/json");
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (callBackResponse != null)
                    callBackResponse.onNetworkFail(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (callBackResponse != null)
                    if (response.isSuccessful())
                        callBackResponse.onSuccess();
                    else
                        callBackResponse.onFail(new Throwable(response.body().string()));
            }
        });
    }
}
