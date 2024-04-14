package com.example.chat;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.databinding.FirstBinding;
import com.example.chat.databinding.HomeFragmentBinding;
import com.example.chat.repo.Repository;
import com.example.chat.repo.SocketClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {


    public static String name;
    private HomeFragmentBinding binding;
    private List<LiveModel> liveModels;
    private List<ItemModel> itemModels;
    private AdapterLive adapterLive;
    private AdapterItem adapterItem;

    private AdapterUsers adapterUsers;
    private List<UserModel> userModels;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        liveModels = new ArrayList<>();
        itemModels = new ArrayList<>();
        userModels = new ArrayList<>();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapterLive = new AdapterLive(liveModels, liveModel -> {
            if (liveModel.getLive_stream_url() == null || liveModel.getLive_stream_url().isEmpty())
                return;
            Bundle bundle = new Bundle();
            bundle.putString("url", liveModel.getLive_stream_url());
            try {
                Navigation.findNavController(getView()).navigate(R.id.action_homeFragment_to_liveFragment, bundle);

            } catch (Exception e) {
            }
        });
        adapterUsers = new AdapterUsers(userModels);
        binding.users.setAdapter(adapterUsers);
        binding.users.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.recLives.setAdapter(adapterLive);
        binding.recLives.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));


        adapterItem = new AdapterItem(itemModels);
        binding.rec.setAdapter(adapterItem);
        binding.rec.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        getItems();
        getLives();

        binding.name.setText(name);
        binding.back.setOnClickListener(v -> {
            try {
                Navigation.findNavController(v).popBackStack();
            } catch (Exception e) {
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).socketClient.addListener(callBack);
        try {
            ((MainActivity) getActivity()).socketClient.setOnline();
        } catch (JSONException e) {
            e.getMessage();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private SocketClient.CallBack callBack = message -> {

        userModels.clear();
        Log.i("TAG", "message : " + message);
        try {
            JSONObject jsonObject = new JSONObject(message);
            if (jsonObject.getString("type").equals("users")) {
                JSONArray jsonArray = jsonObject.getJSONArray("content");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject user = jsonArray.getJSONObject(i);
                    userModels.add(new UserModel(user.getString("name"), user.getString("status").equals("1")));
                }
            }
        } catch (JSONException e) {
            Log.i("TAG", "message : " + e.getMessage());

            e.getMessage();
        }
        try {
            requireActivity().runOnUiThread(() ->adapterUsers.notifyDataSetChanged());
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    private void getItems() {
        if (!itemModels.isEmpty())
            return;
        Repository.getRepository().getItems(new CallBackResponseItems<List<ItemModel>>() {
            @Override
            public void onSuccess(List<ItemModel> list) {
                try {
                    getActivity().runOnUiThread(() -> {
                        itemModels.clear();
                        itemModels.addAll(list);
                        adapterItem.notifyDataSetChanged();
                    });
                } catch (Exception e) {
                }
            }

            @Override
            public void onFail(Throwable throwable) {

            }

            @Override
            public void onNetworkFail(Throwable throwable) {


            }
        });
    }

    private void getLives() {
        if (!liveModels.isEmpty())
            return;
        Repository.getRepository().getLives(new CallBackResponseItems<List<LiveModel>>() {
            @Override
            public void onSuccess(List<LiveModel> list) {
                try {
                    getActivity().runOnUiThread(() -> {

                        liveModels.clear();
                        liveModels.addAll(list);
                        adapterLive.notifyDataSetChanged();
                    });
                } catch (Exception e) {
                }
            }

            @Override
            public void onFail(Throwable throwable) {
            }

            @Override
            public void onNetworkFail(Throwable throwable) {

            }
        });
    }


}
