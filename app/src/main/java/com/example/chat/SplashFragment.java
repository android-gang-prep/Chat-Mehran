package com.example.chat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.chat.databinding.FirstBinding;
import com.example.chat.databinding.SplashBinding;

public class SplashFragment extends Fragment {


    private SplashBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SplashBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        anim();
        new Handler().postDelayed(() -> {
            try {
                Navigation.findNavController(view).navigate(R.id.action_splashFragment_to_firstFragment);
            } catch (Exception e) {
            }
        }, 2000);
    }


    public void anim() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_splash);
        animation.setRepeatCount(1);
        binding.anim.startAnimation(animation);
    }


}
