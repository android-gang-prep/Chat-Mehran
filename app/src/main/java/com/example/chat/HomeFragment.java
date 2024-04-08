package com.example.chat;

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

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {


    public static String name;
    private HomeFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getItems();

        binding.name.setText(name);
        binding.back.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
    }


    private void getItems() {
        Repository.getRepository().getItems(new CallBackResponseItems() {
            @Override
            public void onSuccess(List<ItemModel> list) {
                getActivity().runOnUiThread(() -> {
                    AdapterItem adapterItem = new AdapterItem(list);
                    binding.rec.setAdapter(adapterItem);
                    binding.rec.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                });
            }

            @Override
            public void onFail(Throwable throwable) {
                Log.i("TAG", "onFail: " + throwable.getMessage());
            }

            @Override
            public void onNetworkFail(Throwable throwable) {
                Log.i("TAG", "onNetworkFail: " + throwable.getMessage());

            }
        });
    }
}
