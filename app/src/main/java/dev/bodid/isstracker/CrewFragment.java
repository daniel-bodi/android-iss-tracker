package dev.bodid.isstracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import dev.bodid.isstracker.model.AstronautsResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crew, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.crew_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        TextView headerTextView = view.findViewById(R.id.header_text_view);

        loadCrew(recyclerView, headerTextView);
    }

    private void loadCrew(RecyclerView recyclerView, TextView headerTextView) {
        ApiClient.getCrewApiService().getAstronauts(true).enqueue(new Callback<AstronautsResponse>() {
            @Override
            public void onResponse(@NonNull Call<AstronautsResponse> call, @NonNull Response<AstronautsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AstronautsResponse data = response.body();
                    headerTextView.setText("Jelenleg " + data.getCount() + " ember van az űrben");
                    recyclerView.setAdapter(new AstronautAdapter(data.getResults()));
                } else {
                    headerTextView.setText("Hiba: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AstronautsResponse> call, @NonNull Throwable t) {
                headerTextView.setText("Kapcsolódási hiba: " + t.getMessage());
            }
        });
    }
}
