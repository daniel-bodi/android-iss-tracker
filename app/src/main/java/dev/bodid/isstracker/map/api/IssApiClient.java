package dev.bodid.isstracker.map.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IssApiClient {

    private static final String BASE_URL = "https://api.wheretheiss.at/";

    private static IssApiService apiService;

    private IssApiClient() {}

    public static IssApiService getApiService() {
        if (apiService == null) {
            apiService = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(IssApiService.class);
        }
        return apiService;
    }
}
