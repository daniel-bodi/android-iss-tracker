package dev.bodid.isstracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import dev.bodid.isstracker.model.Astronaut;
import dev.bodid.isstracker.model.AstronautsResponse;
import dev.bodid.isstracker.model.Nationality;
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
        TextView textView = view.findViewById(R.id.crew_text_view);
        loadCrew(textView);
    }

    private void loadCrew(TextView textView) {
        ApiClient.getCrewApiService().getAstronauts(true).enqueue(new Callback<AstronautsResponse>() {
            @Override
            public void onResponse(@NonNull Call<AstronautsResponse> call, @NonNull Response<AstronautsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayCrew(textView, response.body());
                } else {
                    textView.setText("Hiba: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AstronautsResponse> call, @NonNull Throwable t) {
                textView.setText("Kapcsolódási hiba: " + t.getMessage());
            }
        });
    }

    private void displayCrew(TextView textView, AstronautsResponse data) {
        StringBuilder sb = new StringBuilder();
        sb.append("Jelenleg ").append(data.getCount()).append(" ember van az űrben:\n\n");

        for (Astronaut astronaut : data.getResults()) {
            sb.append("• ").append(astronaut.getName()).append("\n");

            if (astronaut.getAgency() != null) {
                sb.append("  Ügynökség: ").append(astronaut.getAgency().getName()).append("\n");
            }

            List<Nationality> nationalities = astronaut.getNationality();
            if (nationalities != null && !nationalities.isEmpty()) {
                sb.append("  Nemzetiség: ");
                for (int i = 0; i < nationalities.size(); i++) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append(nationalities.get(i).getName());
                }
                sb.append("\n");
            }

            sb.append("  Repülések száma: ").append(astronaut.getFlightsCount()).append("\n");
            sb.append("\n");
        }

        textView.setText(sb.toString());
    }
}
