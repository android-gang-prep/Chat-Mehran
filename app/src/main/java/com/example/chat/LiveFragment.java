package com.example.chat;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.navigation.Navigation;

import com.example.chat.databinding.LiveFragmentBinding;

public class LiveFragment extends Fragment {

    LiveFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LiveFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    ExoPlayer player;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        player =
                new ExoPlayer.Builder(getContext())
                        .setMediaSourceFactory(
                                new DefaultMediaSourceFactory(getContext()).setLiveTargetOffsetMs(5000))
                        .build();

        MediaItem mediaItem =
                new MediaItem.Builder()
                        .setUri(getArguments().getString("url"))
                        .setLiveConfiguration(
                                new MediaItem.LiveConfiguration.Builder().build())
                        .build();
        player.setMediaItem(mediaItem);
        binding.player.setPlayer(player);
        binding.player.setUseController(false);
        player.prepare();
        player.play();
        binding.close.setOnClickListener(v -> {
            try {
                Navigation.findNavController(v).popBackStack();
            }catch (Exception e){}
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        player.play();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }
}
