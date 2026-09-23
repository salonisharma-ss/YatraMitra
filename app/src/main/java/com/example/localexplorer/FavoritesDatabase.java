package com.example.localexplorer;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;

public class FavoritesDatabase {
    private static final String PREF_NAME = "favorites_pref";
    private SharedPreferences sharedPreferences;

    public FavoritesDatabase(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private String getUserKey() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getUid() : "guest";
    }

    public void addFavorite(String placeName, String address) {
        if (placeName == null || placeName.isEmpty()) return;
        if (address == null) address = "No address";
        
        sharedPreferences.edit().putString(getUserKey() + "_" + placeName, address).apply();
    }

    public List<String> getAllFavorites() {
        List<String> favorites = new ArrayList<>();
        java.util.Map<String, ?> allEntries = sharedPreferences.getAll();
        String userPrefix = getUserKey() + "_";

        for (java.util.Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith(userPrefix)) {
                favorites.add(entry.getKey().substring(userPrefix.length()));
            }
        }
        return favorites;
    }

    public String getFavoriteAddress(String placeName) {
        if (placeName == null) return "";
        return sharedPreferences.getString(getUserKey() + "_" + placeName, "");
    }

    public void removeFavorite(String placeName) {
        if (placeName == null) return;
        sharedPreferences.edit().remove(getUserKey() + "_" + placeName).apply();
    }
}
