package dev.bodid.isstracker.crew.api;

import dev.bodid.isstracker.crew.model.AstronautsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CrewApiService {

    @GET("astronauts/")
    Call<AstronautsResponse> getAstronauts(@Query("in_space") boolean inSpace);
}
