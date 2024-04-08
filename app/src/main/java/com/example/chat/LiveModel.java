package com.example.chat;

public class LiveModel {
    private String profile_image;
    private String live_stream_url;

    public LiveModel(String profile_image, String live_stream_url) {
        this.profile_image = profile_image;
        this.live_stream_url = live_stream_url;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getLive_stream_url() {
        return live_stream_url;
    }

    public void setLive_stream_url(String live_stream_url) {
        this.live_stream_url = live_stream_url;
    }
}
