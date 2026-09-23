package com.example.localexplorer;

public class FavoriteModel {
    private String name;
    private String address;

    public FavoriteModel() {}

    public FavoriteModel(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}