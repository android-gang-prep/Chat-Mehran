package com.example.chat;

import java.util.List;

public class ListItemModel {
    private List<ItemModel> favourites;

    public ListItemModel(List<ItemModel> favourites) {
        this.favourites = favourites;
    }

    public List<ItemModel> getFavourites() {
        return favourites;
    }

    public void setFavourites(List<ItemModel> favourites) {
        this.favourites = favourites;
    }
}
