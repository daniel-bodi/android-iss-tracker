package dev.bodid.isstracker.crew.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Astronaut {

    @SerializedName("name")
    private String name;

    @SerializedName("nationality")
    private List<Nationality> nationality;

    @SerializedName("bio")
    private String bio;

    @SerializedName("profile_image")
    private String profileImage;

    @SerializedName("profile_image_thumbnail")
    private String profileImageThumbnail;

    @SerializedName("flights_count")
    private int flightsCount;

    @SerializedName("spacewalks_count")
    private int spacewalksCount;

    @SerializedName("date_of_birth")
    private String dateOfBirth;

    @SerializedName("agency")
    private Agency agency;

    public String getName() {
        return name;
    }

    public List<Nationality> getNationality() {
        return nationality;
    }

    public String getBio() {
        return bio;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getProfileImageThumbnail() {
        return profileImageThumbnail;
    }

    public int getFlightsCount() {
        return flightsCount;
    }

    public int getSpacelwalksCount() {
        return spacewalksCount;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public Agency getAgency() {
        return agency;
    }
}
