package dev.bodid.isstracker.pass;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.Locale;

import dev.bodid.isstracker.BuildConfig;
import dev.bodid.isstracker.R;
import dev.bodid.isstracker.pass.api.PassApiClient;
import dev.bodid.isstracker.pass.model.PassResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassFragment extends Fragment {

    private FusedLocationProviderClient fusedLocationClient;
    private TextView coordsTextView;
    private RecyclerView recyclerView;
    private double userLat = Double.NaN;
    private double userLon = Double.NaN;

    private LocationCallback locationCallback;

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    fetchLocation();
                } else {
                    showManualInputDialog();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pass, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        coordsTextView = view.findViewById(R.id.coords_text_view);
        recyclerView = view.findViewById(R.id.pass_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        Button locationButton = view.findViewById(R.id.location_button);
        locationButton.setOnClickListener(v -> requestLocation());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopLocationUpdates();
        coordsTextView = null;
        recyclerView = null;
    }

    private void requestLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fetchLocation();
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void fetchLocation() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                stopLocationUpdates();
                if (result.getLastLocation() != null) {
                    setLocation(result.getLastLocation().getLatitude(), result.getLastLocation().getLongitude());
                } else {
                    showManualInputDialog();
                }
            }
        };

        LocationRequest request = new LocationRequest.Builder(0)
                .setMaxUpdates(1)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            locationCallback = null;
        }
    }

    private void setLocation(double lat, double lon) {
        userLat = lat;
        userLon = lon;
        if (coordsTextView != null) {
            coordsTextView.setText(String.format(Locale.US, "%.4f°, %.4f°", lat, lon));
        }
        fetchPasses();
    }

    private void fetchPasses() {
        PassApiClient.getApiService()
                .getPasses(userLat, userLon, BuildConfig.N2YO_API_KEY)
                .enqueue(new Callback<PassResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PassResponse> call, @NonNull Response<PassResponse> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(getContext(), "API hiba: " + response.code(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (response.body() == null || response.body().getPasses() == null || response.body().getPasses().isEmpty()) {
                            Toast.makeText(getContext(), "Nincs átrepülés a következő 5 napban", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        showPasses(response.body());
                    }

                    @Override
                    public void onFailure(@NonNull Call<PassResponse> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "Hálózati hiba: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showPasses(PassResponse response) {
        if (recyclerView == null) {
            return;
        }
        recyclerView.setAdapter(new PassAdapter(response.getPasses()));
    }

    private void showManualInputDialog() {
        if (!isAdded()) {
            return;
        }
        int dp16 = (int) (16 * getResources().getDisplayMetrics().density);

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp16, dp16, dp16, 0);

        EditText latInput = new EditText(requireContext());
        latInput.setHint("Szélességi fok (pl. 47.4979)");
        latInput.setInputType(InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_DECIMAL
                | InputType.TYPE_NUMBER_FLAG_SIGNED);

        EditText lonInput = new EditText(requireContext());
        lonInput.setHint("Hosszúsági fok (pl. 19.0402)");
        lonInput.setInputType(InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_DECIMAL
                | InputType.TYPE_NUMBER_FLAG_SIGNED);

        layout.addView(latInput);
        layout.addView(lonInput);

        new AlertDialog.Builder(requireContext())
                .setTitle("Helyzet megadása")
                .setView(layout)
                .setPositiveButton("OK", (dialog, which) -> {
                    try {
                        double lat = Double.parseDouble(latInput.getText().toString());
                        double lon = Double.parseDouble(lonInput.getText().toString());
                        setLocation(lat, lon);
                    } catch (NumberFormatException e) {
                        // érvénytelen bemenet, nem csinálunk semmit
                    }
                })
                .setNegativeButton("Mégse", null)
                .show();
    }

    public double getUserLat() {
        return userLat;
    }

    public double getUserLon() {
        return userLon;
    }
}
