package com.example.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.chat.databinding.FirstBinding;

public class FirstFragment extends Fragment {


    private FirstBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.signup.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_firstFragment_to_signUpFragment));
        binding.login.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_firstFragment_to_loginFragment));
    }
}
