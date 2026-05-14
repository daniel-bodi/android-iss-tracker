package dev.bodid.isstracker.crew.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CrewApiClient {

    private static final String BASE_URL = "https://lldev.thespacedevs.com/2.3.0/";

    private static CrewApiService apiService;

    private CrewApiClient() {}

    public static CrewApiService getApiService() {
        if (apiService == null) {
            apiService = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(CrewApiService.class);
        }
        return apiService;
    }
}
