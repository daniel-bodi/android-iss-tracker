package dev.bodid.isstracker.pass.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PassApiClient {

    private static final String BASE_URL = "https://api.n2yo.com/";

    private static PassApiService apiService;

    private PassApiClient() {}

    public static PassApiService getApiService() {
        if (apiService == null) {
            apiService = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(PassApiService.class);
        }
        return apiService;
    }
}
