package com.example.chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chat.databinding.ChatFragmentBinding;
import com.example.chat.repo.SocketClient;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ChatFragment extends Fragment {

    private ChatFragmentBinding binding;
    private Gson gson;
    private List<MessageModel> messages;

    private String name;
    ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), o -> {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(o);
                byte[] bytes = ByteStreams.toByteArray(inputStream);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                App.getApp().socketClient.sendImage(HomeFragment.name, name, outputStream.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
        }
    });
    private AdapterMessage adapterMessage;
    private HashMap<String, byte[]> images;
    SocketClient.CallBack callBack = message -> {

        try {
            JSONObject jsonObject = new JSONObject(message);
            if (jsonObject.getString("type").equals("message")) {
                MessageModel messageModel = gson.fromJson(message, MessageModel.class);
                if ((messageModel.getName().equals(HomeFragment.name) && messageModel.getTo().equals(name)) || (messageModel.getName().equals(name) && messageModel.getTo().equals(HomeFragment.name)))
                    messages.add(messageModel);
            } else if (jsonObject.getString("type").equals("image")) {
                MessageModel messageModel = gson.fromJson(message, MessageModel.class);
                if ((messageModel.getName().equals(HomeFragment.name) && messageModel.getTo().equals(name)) || (messageModel.getName().equals(name) && messageModel.getTo().equals(HomeFragment.name))) {
                    byte[] bytes = Base64.decode(messageModel.getContent(), Base64.DEFAULT);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    try {
                        if (images.containsKey(messageModel.getId()))
                            outputStream.write(images.get(messageModel.getId()));
                        outputStream.write(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    byte[] bytes1 = outputStream.toByteArray();

                    images.put(messageModel.getId(), bytes1);
                    if (messageModel.getStep().equals("end")) {
                        images.remove(messageModel.getId());
                        messageModel.setContent(Base64.encodeToString(bytes1, Base64.DEFAULT));
                        messages.add(messageModel);
                    }
                }

            }
            requireActivity().runOnUiThread(() -> adapterMessage.notifyDataSetChanged());
        } catch (JSONException e) {
            e.getMessage();
        }
    };
    private HashMap<String, List<Byte>> voices;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        messages = new ArrayList<>();
        images = new HashMap<>();
        voices = new HashMap<>();
        if (savedInstanceState != null)
            try {
                messages.addAll(Arrays.asList(gson.fromJson(savedInstanceState.getString("messages"), MessageModel[].class)));
            }catch (Exception e){}
        name = getArguments().getString("name");


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("messages", gson.toJson(messages));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ChatFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapterMessage = new AdapterMessage(messages);
        binding.rec.setAdapter(adapterMessage);
        binding.rec.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.toolbar.setTitle(name);
        binding.send.setOnClickListener(v -> {
            if (binding.message.getText().toString().trim().isEmpty())
                return;


            MessageModel messageModel = new MessageModel(HomeFragment.name, name, binding.message.getText().toString().trim(), "");
            App.getApp().socketClient.sendMessage(messageModel);
            binding.message.setText("");

        });

        binding.sendPic.setOnClickListener(v -> activityResultLauncher.launch("image/*"));

    }

    @Override
    public void onResume() {
        super.onResume();
        App.getApp().socketClient.addListener(callBack);
    }

    @Override
    public void onPause() {
        super.onPause();
        App.getApp().socketClient.removeListener(callBack);
    }
}
