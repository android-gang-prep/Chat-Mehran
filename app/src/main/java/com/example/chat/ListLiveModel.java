package com.example.chat;

import java.util.List;

public class ListLiveModel {
    private List<LiveModel> lives;

    public ListLiveModel(List<LiveModel> lives) {
        this.lives = lives;
    }

    public List<LiveModel> getLives() {
        return lives;
    }

    public void setLives(List<LiveModel> lives) {
        this.lives = lives;
    }
}
