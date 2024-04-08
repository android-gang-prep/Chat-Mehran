package com.example.chat;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.chat.databinding.LoginBinding;
import com.example.chat.databinding.SignupBinding;
import com.example.chat.repo.Repository;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends Fragment {



    LoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.back.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        binding.login.setOnClickListener(v -> Login());
    }







    private void Login() {
        binding.email.setError(null);
        binding.password.setError(null);

        boolean signup = true;
        if (binding.email.getText().toString().trim().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString().trim()).matches()) {
            binding.email.setError("invalid email address");
            signup = false;
        }


        if (binding.password.getText().toString().isEmpty() || binding.password.getText().toString().length() < 10) {
            binding.password.setError("password should more then 10 digits");
            signup = false;
        }


        if (!signup)
            return;

        binding.login.setEnabled(false);

        Repository.getRepository().Login(binding.email.getText().toString().toString(), binding.password.getText().toString().toString(), new CallBackResponse() {
            @Override
            public void onSuccess() {

                getActivity().runOnUiThread(() -> {
                    HomeFragment.name=binding.email.getText().toString().toString();
                    Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_homeFragment);
                    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                });

            }

            @Override
            public void onFail(Throwable throwable) {
                getActivity().runOnUiThread(() -> {
                    binding.login.setEnabled(true);
                    try {
                        Toast.makeText(getContext(), new JSONObject(throwable.getMessage()).getString("error"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.getMessage();
                        Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNetworkFail(Throwable throwable) {
                getActivity().runOnUiThread(() -> {
                    binding.login.setEnabled(true);
                    Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
