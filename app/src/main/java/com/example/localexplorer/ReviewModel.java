package com.example.localexplorer;

public class ReviewModel {
    private String userName;
    private String userEmail;
    private double rating;
    private String reviewText;
    private String date;

    public ReviewModel() {}

    public ReviewModel(String userName, String userEmail, double rating, String reviewText, String date) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.rating = rating;
        this.reviewText = reviewText;
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}