package dev.bodid.isstracker.map.api;

import dev.bodid.isstracker.map.model.IssNowResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface IssApiService {

    @GET("v1/satellites/25544")
    Call<IssNowResponse> getIssPosition();
}
