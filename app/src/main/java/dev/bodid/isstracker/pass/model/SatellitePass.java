package dev.bodid.isstracker.pass.model;

import com.google.gson.annotations.SerializedName;

public class SatellitePass {

    @SerializedName("startUTC")
    private long startUTC;

    @SerializedName("maxEl")
    private double maxEl;

    @SerializedName("maxAzCompass")
    private String maxAzCompass;

    @SerializedName("duration")
    private int duration;

    public long getStartUTC() {
        return startUTC;
    }

    public double getMaxEl() {
        return maxEl;
    }

    public String getMaxAzCompass() {
        return maxAzCompass;
    }

    public int getDuration() {
        return duration;
    }
}
