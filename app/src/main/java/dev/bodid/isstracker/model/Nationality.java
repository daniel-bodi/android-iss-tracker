package dev.bodid.isstracker.model;

import com.google.gson.annotations.SerializedName;

public class Nationality {

    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }
}
