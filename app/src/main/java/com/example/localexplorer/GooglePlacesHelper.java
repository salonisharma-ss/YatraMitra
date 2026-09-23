package com.example.localexplorer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.libraries.places.api.Places;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GooglePlacesHelper {

    private static final String TAG = "GooglePlacesHelper";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .build();

    // Replace with your API key
    private static String API_KEY = "YOUR_GOOGLE_MAPS_API_KEY";

    public static void initializePlaces(Context context, String apiKey) {
        API_KEY = apiKey;
        try {
            if (!Places.isInitialized()) {
                Places.initialize(context, apiKey);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Places: " + e.getMessage());
        }
    }

    public static void searchNearbyPlaces(Context context, Location location,
                                          String placeType, PlacesCallback callback) {
        if (location == null) {
            callback.onError("Location is null");
            return;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            callback.onError("Location permission not granted");
            return;
        }
        new Thread(() -> {
            try {
                List<PlaceModel> places = fetchNearbyPlacesFromAPI(
                        location.getLatitude(),
                        location.getLongitude(),
                        placeType
                );
                callback.onSuccess(places);
            } catch (Exception e) {
                Log.e(TAG, "Error: " + e.getMessage());
                callback.onError(e.getMessage());
            }
        }).start();
    }

    private static List<PlaceModel> fetchNearbyPlacesFromAPI(double latitude, double longitude, String placeType) throws IOException {
        List<PlaceModel> places = new ArrayList<>();
        String apiType = convertPlaceType(placeType);
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + latitude + "," + longitude +
                "&radius=2500" +
                "&type=" + apiType +
                "&key=" + API_KEY;

        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response.code());
            String responseBody = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            if (jsonObject.has("status")) {
                String status = jsonObject.get("status").getAsString();
                if (!status.equals("OK") && !status.equals("ZERO_RESULTS")) throw new IOException("API returned: " + status);
            }
            JsonArray resultsArray = jsonObject.getAsJsonArray("results");
            if (resultsArray != null) {
                for (int i = 0; i < resultsArray.size(); i++) {
                    JsonObject result = resultsArray.get(i).getAsJsonObject();
                    PlaceModel place = parsePlaceFromJson(result);
                    if (place != null) {
                        place.setDistance(calculateDistance(latitude, longitude, place.getLatitude(), place.getLongitude()));
                        places.add(place);
                    }
                }
            }
        }
        return places;
    }

    private static PlaceModel parsePlaceFromJson(JsonObject obj) {
        try {
            PlaceModel place = new PlaceModel();
            place.setName(obj.has("name") ? obj.get("name").getAsString() : "Unknown Place");
            place.setPlaceId(obj.has("place_id") ? obj.get("place_id").getAsString() : null);
            place.setAddress(obj.has("vicinity") ? obj.get("vicinity").getAsString() : (obj.has("formatted_address") ? obj.get("formatted_address").getAsString() : "No address"));
            
            if (obj.has("geometry")) {
                JsonObject loc = obj.getAsJsonObject("geometry").getAsJsonObject("location");
                place.setLatitude(loc.get("lat").getAsDouble());
                place.setLongitude(loc.get("lng").getAsDouble());
            }

            place.setRating(obj.has("rating") ? obj.get("rating").getAsDouble() : 4.0);
            place.setReviews(obj.has("user_ratings_total") ? obj.get("user_ratings_total").getAsInt() : 0);
            place.setPrice(obj.has("price_level") ? getPriceString(obj.get("price_level").getAsInt()) : "₹ Budget");

            // Photo URL
            if (obj.has("photos")) {
                JsonArray photos = obj.getAsJsonArray("photos");
                if (photos.size() > 0) {
                    String ref = photos.get(0).getAsJsonObject().get("photo_reference").getAsString();
                    place.setPhotoUrl("https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference=" + ref + "&key=" + API_KEY);
                }
            }
            return place;
        } catch (Exception e) {
            return null;
        }
    }

    public static void searchPlacesByText(Context context, String query, PlacesCallback callback) {
        new Thread(() -> {
            try {
                String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + query.replaceAll(" ", "+") + "&key=" + API_KEY;
                Request request = new Request.Builder().url(url).build();
                try (Response response = client.newCall(request).execute()) {
                    String responseBody = response.body().string();
                    JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                    JsonArray resultsArray = jsonObject.getAsJsonArray("results");
                    List<PlaceModel> places = new ArrayList<>();
                    if (resultsArray != null) {
                        for (int i = 0; i < resultsArray.size(); i++) {
                            PlaceModel place = parsePlaceFromJson(resultsArray.get(i).getAsJsonObject());
                            if (place != null) places.add(place);
                        }
                    }
                    callback.onSuccess(places);
                }
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public static void getPlaceDetails(Context context, String placeId, PlaceDetailsCallback callback) {
        new Thread(() -> {
            try {
                String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + placeId + "&fields=reviews&key=" + API_KEY;
                Request request = new Request.Builder().url(url).build();
                try (Response response = client.newCall(request).execute()) {
                    String responseBody = response.body().string();
                    JsonObject result = JsonParser.parseString(responseBody).getAsJsonObject().getAsJsonObject("result");
                    List<ReviewModel> reviews = new ArrayList<>();
                    if (result != null && result.has("reviews")) {
                        JsonArray arr = result.getAsJsonArray("reviews");
                        for (int i = 0; i < arr.size(); i++) {
                            JsonObject r = arr.get(i).getAsJsonObject();
                            ReviewModel rm = new ReviewModel();
                            rm.setUserName(r.get("author_name").getAsString());
                            rm.setRating(r.get("rating").getAsDouble());
                            rm.setReviewText(r.get("text").getAsString());
                            reviews.add(rm);
                        }
                    }
                    callback.onSuccess(reviews);
                }
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }

    private static String convertPlaceType(String placeType) {
        return placeType.toLowerCase().equals("lodging") ? "lodging" : placeType.toLowerCase();
    }

    private static String getPriceString(int level) {
        switch (level) {
            case 1: return "₹ Budget";
            case 2: return "₹₹ Mid-range";
            case 3: return "₹₹₹ Premium";
            case 4: return "₹₹₹₹ Luxury";
            default: return "₹ Budget";
        }
    }

    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0] / 1000.0;
    }

    public interface PlaceDetailsCallback { void onSuccess(List<ReviewModel> reviews); void onError(String error); }
    public interface PlacesCallback { void onSuccess(List<PlaceModel> places); void onError(String error); }
}
