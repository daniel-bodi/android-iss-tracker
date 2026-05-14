package dev.bodid.isstracker.crew;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.DateFormat;
import java.util.Date;

import dev.bodid.isstracker.R;
import dev.bodid.isstracker.crew.api.CrewApiClient;
import dev.bodid.isstracker.crew.cache.CrewCache;
import dev.bodid.isstracker.crew.model.AstronautsResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrewFragment extends Fragment {

    private CrewCache cache;
    private TextView headerTextView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crew, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.crew_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        headerTextView = view.findViewById(R.id.header_text_view);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireContext(), R.color.cyan_primary)
        );
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(
                ContextCompat.getColor(requireContext(), R.color.space_navy)
        );
        swipeRefreshLayout.setOnRefreshListener(this::loadFromNetwork);

        cache = new CrewCache(requireContext());

        if (cache.hasCache()) {
            showData(cache.load(), true);
        } else {
            loadFromNetwork();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        headerTextView = null;
        recyclerView = null;
        swipeRefreshLayout = null;
    }

    private void loadFromNetwork() {
        CrewApiClient.getApiService().getAstronauts(true).enqueue(new Callback<AstronautsResponse>() {
            @Override
            public void onResponse(@NonNull Call<AstronautsResponse> call, @NonNull Response<AstronautsResponse> response) {
                stopRefreshing();
                if (response.isSuccessful() && response.body() != null) {
                    cache.save(response.body());
                    showData(response.body(), false);
                } else {
                    showNetworkError("Hiba: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AstronautsResponse> call, @NonNull Throwable t) {
                stopRefreshing();
                showNetworkError("Nincs kapcsolat");
            }
        });
    }

    private void stopRefreshing() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showData(AstronautsResponse data, boolean fromCache) {
        if (headerTextView == null) {
            return;
        }
        String header = "Jelenleg " + data.getCount() + " ember van az űrben";
        if (fromCache) {
            String date = DateFormat.getDateTimeInstance().format(new Date(cache.getTimestamp()));
            header += "\n(mentett adat: " + date + ")";
        }
        headerTextView.setText(header);
        recyclerView.setAdapter(new AstronautAdapter(data.getResults()));
    }

    private void showNetworkError(String message) {
        if (!cache.hasCache() && headerTextView != null) {
            headerTextView.setText(message);
        }
    }
}
