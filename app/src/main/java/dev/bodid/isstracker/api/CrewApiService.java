package dev.bodid.isstracker.api;

import dev.bodid.isstracker.model.AstronautsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CrewApiService {

    @GET("astronauts/")
    Call<AstronautsResponse> getAstronauts(@Query("in_space") boolean inSpace);
}
