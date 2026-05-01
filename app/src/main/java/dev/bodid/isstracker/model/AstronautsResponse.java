package dev.bodid.isstracker.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AstronautsResponse {

    @SerializedName("count")
    private int count;

    @SerializedName("results")
    private List<Astronaut> results;

    public int getCount() {
        return count;
    }

    public List<Astronaut> getResults() {
        return results;
    }
}
