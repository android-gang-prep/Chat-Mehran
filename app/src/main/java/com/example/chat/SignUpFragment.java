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

        binding.back.setOnClickListener(v -> {
            try {
                Navigation.findNavController(v).popBackStack();
            }catch (Exception e){}
        });
        binding.signup.setOnClickListener(v -> {
            if (binding.signup.getText().toString().trim().equals("Activate Account"))
                active();
            else SignUP();
        });
    }

    private void active() {
        if (binding.active.getText().toString().trim().length() < 4) {
            binding.activeI.setError("Code is incorrect");
            return;
        }
        binding.signup.setEnabled(false);
        binding.signup.setVisibility(View.INVISIBLE);
        binding.progress.setVisibility(View.VISIBLE);
        Repository.getRepository().activeCode(binding.active.getText().toString().trim(), new CallBackResponse() {
            @Override
            public void onSuccess() {
              try {
                  getActivity().runOnUiThread(() -> {
                      try {
                          ((MainActivity) getActivity()).socketClient.start(binding.ip.getText().toString().trim(), binding.email.getText().toString().trim());
                          Navigation.findNavController(getView()).navigate(R.id.action_signUpFragment_to_homeFragment);
                      }catch (Exception e){}
                      Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                  });
              }catch (Exception e){}
                Log.i("TAG", "onSuccess: ");
            }

            @Override
            public void onFail(Throwable throwable) {
               try {
                   getActivity().runOnUiThread(() -> {
                       binding.signup.setVisibility(View.VISIBLE);
                       binding.progress.setVisibility(View.GONE);
                       binding.signup.setEnabled(true);
                       try {
                           Toast.makeText(getContext(), new JSONObject(throwable.getMessage()).getString("error"), Toast.LENGTH_SHORT).show();
                       } catch (JSONException e) {
                           e.getMessage();
                           Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                       }
                   });
               }catch (Exception e){}
            }

            @Override
            public void onNetworkFail(Throwable throwable) {
               try {
                   getActivity().runOnUiThread(() -> {
                       binding.signup.setVisibility(View.VISIBLE);
                       binding.progress.setVisibility(View.GONE);
                       binding.signup.setEnabled(true);
                       Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                   });
               }catch (Exception e){}
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
        binding.emailL.setError(null);
        binding.passwordL.setError(null);
        binding.nameL.setError(null);
        binding.phoneL.setError(null);

        boolean signup = true;
        if (binding.email.getText().toString().trim().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString().trim()).find()) {
            binding.emailL.setError("invalid email address");
            signup = false;
        }


        if (binding.password.getText().toString().isEmpty() || binding.password.getText().toString().length() < 10) {
            binding.passwordL.setError("password should more then 10 digits");
            signup = false;
        }

        if (binding.name.getText().toString().isEmpty() || binding.name.getText().toString().length() < 3) {
            binding.nameL.setError("please fill name");
            signup = false;
        }

        if (binding.phone.getText().toString().trim().isEmpty() || !isPhoneNumber(binding.phone.getText().toString().trim())) {
            binding.phoneL.setError("invalid phone number");
            signup = false;
        }
        if (binding.ip.getText().toString().trim().isEmpty() || !Patterns.IP_ADDRESS.matcher(binding.ip.getText().toString().trim()).matches()) {
            binding.ipL.setError("ip address is invalid");
            signup = false;
        }
        if (!signup)
            return;

        binding.signup.setEnabled(false);

        binding.signup.setVisibility(View.INVISIBLE);
        binding.progress.setVisibility(View.VISIBLE);
        Repository.getRepository().Signup(binding.email.getText().toString().toString(), binding.name.getText().toString().toString(), binding.password.getText().toString().toString(), binding.phone.getText().toString().toString(), new CallBackResponse() {
            @Override
            public void onSuccess() {
                String[] phone = getPhone(binding.phone.getText().toString().trim());
            try {
                getActivity().runOnUiThread(() -> {
                    HomeFragment.name = binding.name.getText().toString().toString();

                    binding.signup.setEnabled(true);
                    binding.linearLayout.setVisibility(View.GONE);
                    binding.activeL.setVisibility(View.VISIBLE);
                    binding.signup.setText("Activate Account");
                    binding.signup.setVisibility(View.VISIBLE);
                    binding.progress.setVisibility(View.GONE);
                });
                Repository.getRepository().sendCode(phone[0], phone[1], null);
            }catch (Exception e){}

            }

            @Override
            public void onFail(Throwable throwable) {
              try {
                  getActivity().runOnUiThread(() -> {

                      binding.signup.setVisibility(View.VISIBLE);
                      binding.progress.setVisibility(View.GONE);
                      binding.signup.setEnabled(true);
                      try {
                          Toast.makeText(getContext(), new JSONObject(throwable.getMessage()).getString("error"), Toast.LENGTH_SHORT).show();
                      } catch (JSONException e) {
                          e.getMessage();
                          Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                      }
                  });
              }catch (Exception e){}
            }

            @Override
            public void onNetworkFail(Throwable throwable) {
                Log.i("TAG", "onNetworkFail: " + throwable.getMessage());
               try {
                   getActivity().runOnUiThread(() -> {

                       binding.signup.setVisibility(View.VISIBLE);
                       binding.progress.setVisibility(View.GONE);
                       binding.signup.setEnabled(true);
                       Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                   });
               }catch (Exception e){}
            }
        });
    }
}
