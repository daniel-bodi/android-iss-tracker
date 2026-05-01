package dev.bodid.isstracker.pass.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PassResponse {

    @SerializedName("passes")
    private List<SatellitePass> passes;

    public List<SatellitePass> getPasses() {
        return passes;
    }
}
