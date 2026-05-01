package dev.bodid.isstracker.pass.api;

import dev.bodid.isstracker.pass.model.PassResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PassApiService {

    @GET("rest/v1/satellite/visualpasses/25544/{lat}/{lon}/0/10/1/")
    Call<PassResponse> getPasses(
            @Path("lat") double lat,
            @Path("lon") double lon,
            @Query("apiKey") String apiKey
    );
}
