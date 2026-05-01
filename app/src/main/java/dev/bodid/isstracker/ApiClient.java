package dev.bodid.isstracker;

import dev.bodid.isstracker.api.CrewApiService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String CREW_BASE_URL = "https://lldev.thespacedevs.com/2.3.0/";

    private static CrewApiService crewApiService;

    private ApiClient() {}

    public static CrewApiService getCrewApiService() {
        if (crewApiService == null) {
            crewApiService = new Retrofit.Builder()
                    .baseUrl(CREW_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(CrewApiService.class);
        }
        return crewApiService;
    }
}
