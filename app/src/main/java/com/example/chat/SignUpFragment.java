package com.example.chat;

import android.os.Bundle;
import android.os.PatternMatcher;
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

import com.example.chat.databinding.SignupBinding;
import com.example.chat.repo.Repository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class SignUpFragment extends Fragment {


    SignupBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SignupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.back.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        binding.signup.setOnClickListener(v -> {
            if (binding.signup.getText().toString().trim().equals("Activate Account"))
                active();
            else SignUP();
        });
    }

    private void active() {
        if (binding.active.getText().toString().trim().length() < 4) {
            binding.active.setError("Code is incorrect");
            return;
        }
        binding.signup.setEnabled(false);

        Repository.getRepository().activeCode(binding.active.getText().toString().trim(), new CallBackResponse() {
            @Override
            public void onSuccess() {
                getActivity().runOnUiThread(() -> {
                    Navigation.findNavController(getView()).navigate(R.id.action_signUpFragment_to_homeFragment);
                    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                });
                Log.i("TAG", "onSuccess: ");
            }

            @Override
            public void onFail(Throwable throwable) {
                getActivity().runOnUiThread(() -> {
                    binding.signup.setEnabled(true);
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
                    binding.signup.setEnabled(true);
                    Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                });
                Log.i("TAG", "onNetworkFail: " + throwable.getMessage());
            }
        });


    }


    private boolean isPhoneNumber(String phone) {
        String[] strings = getResources().getStringArray(R.array.CountryCodes);

        for (String code : strings) {
            if (phone.startsWith(code.split(",")[0]))
                return true;
        }
        return false;
    }

    private String[] getPhone(String phone) {
        String[] strings = getResources().getStringArray(R.array.CountryCodes);

        for (String code : strings) {
            if (phone.startsWith(code.split(",")[0]))
                return new String[]{code.split(",")[0], phone.replace(code.split(",")[0], "")};
        }
        return new String[0];
    }

    private void SignUP() {
        binding.email.setError(null);
        binding.password.setError(null);
        binding.name.setError(null);
        binding.phone.setError(null);

        boolean signup = true;
        if (binding.email.getText().toString().trim().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString().trim()).find()) {
            binding.email.setError("invalid email address");
            signup = false;
        }


        if (binding.password.getText().toString().isEmpty() || binding.password.getText().toString().length() < 10) {
            binding.password.setError("password should more then 10 digits");
            signup = false;
        }

        if (binding.name.getText().toString().isEmpty() || binding.name.getText().toString().length() < 3) {
            binding.name.setError("please fill name");
            signup = false;
        }

        if (binding.phone.getText().toString().trim().isEmpty() || !isPhoneNumber(binding.phone.getText().toString().trim())) {
            binding.phone.setError("invalid phone number");
            signup = false;
        }

        if (!signup)
            return;

        binding.signup.setEnabled(false);

        Repository.getRepository().Signup(binding.email.getText().toString().toString(), binding.name.getText().toString().toString(), binding.password.getText().toString().toString(), binding.phone.getText().toString().toString(), new CallBackResponse() {
            @Override
            public void onSuccess() {
                String[] phone = getPhone(binding.phone.getText().toString().trim());
                getActivity().runOnUiThread(() -> {
                    HomeFragment.name = binding.name.getText().toString().toString();

                    binding.signup.setEnabled(true);
                    binding.linearLayout.setVisibility(View.GONE);
                    binding.activeL.setVisibility(View.VISIBLE);
                    binding.signup.setText("Activate Account");
                });
                Repository.getRepository().sendCode(phone[0], phone[1], null);

            }

            @Override
            public void onFail(Throwable throwable) {
                getActivity().runOnUiThread(() -> {
                    binding.signup.setEnabled(true);
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
                Log.i("TAG", "onNetworkFail: " + throwable.getMessage());
                getActivity().runOnUiThread(() -> {
                    binding.signup.setEnabled(true);
                    Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
