package dev.bodid.isstracker.model;

import com.google.gson.annotations.SerializedName;

public class Agency {

    @SerializedName("name")
    private String name;

    @SerializedName("abbrev")
    private String abbrev;

    @SerializedName("country_code")
    private String countryCode;

    public String getName() {
        return name;
    }

    public String getAbbrev() {
        return abbrev;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
