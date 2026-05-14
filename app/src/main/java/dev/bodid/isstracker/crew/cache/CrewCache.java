package dev.bodid.isstracker.crew.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import dev.bodid.isstracker.crew.model.AstronautsResponse;

public class CrewCache {

    private static final String PREF_NAME = "crew_cache";
    private static final String KEY_JSON = "crew_json";
    private static final String KEY_TIMESTAMP = "crew_timestamp";

    private final SharedPreferences prefs;
    private final Gson gson;

    public CrewCache(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void save(AstronautsResponse response) {
        prefs.edit()
                .putString(KEY_JSON, gson.toJson(response))
                .putLong(KEY_TIMESTAMP, System.currentTimeMillis())
                .apply();
    }

    public AstronautsResponse load() {
        String json = prefs.getString(KEY_JSON, null);
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, AstronautsResponse.class);
    }

    public boolean hasCache() {
        return prefs.contains(KEY_JSON);
    }

    public long getTimestamp() {
        return prefs.getLong(KEY_TIMESTAMP, 0);
    }
}
