package dev.bodid.isstracker.map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dev.bodid.isstracker.R;
import dev.bodid.isstracker.map.api.IssApiClient;
import dev.bodid.isstracker.map.model.IssNowResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int REFRESH_INTERVAL_MS = 5000;
    private static final int MAX_HISTORY_SIZE = 1100;

    private GoogleMap googleMap;
    private TextView latTextView;
    private TextView lonTextView;
    private TextView speedTextView;
    private BitmapDescriptor issMarkerIcon;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<LatLng> positionHistory = new ArrayList<>();
    private boolean isRefreshing = false;

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            fetchIssPosition();
            handler.postDelayed(this, REFRESH_INTERVAL_MS);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        latTextView = view.findViewById(R.id.lat_text_view);
        lonTextView = view.findViewById(R.id.lon_text_view);
        speedTextView = view.findViewById(R.id.speed_text_view);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_container);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .add(R.id.map_container, mapFragment)
                    .commit();
        }

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 2));
        issMarkerIcon = createIssMarkerIcon();
        startRefreshing();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (googleMap != null && !isHidden()) {
            startRefreshing();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRefreshing();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            stopRefreshing();
        } else if (googleMap != null) {
            startRefreshing();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopRefreshing();
        latTextView = null;
        lonTextView = null;
        speedTextView = null;
    }

    private void startRefreshing() {
        if (isRefreshing) {
            return;
        }
        isRefreshing = true;
        handler.post(refreshRunnable);
    }

    private void stopRefreshing() {
        isRefreshing = false;
        handler.removeCallbacks(refreshRunnable);
    }

    private void fetchIssPosition() {
        IssApiClient.getApiService().getIssPosition().enqueue(new Callback<IssNowResponse>() {
            @Override
            public void onResponse(@NonNull Call<IssNowResponse> call, @NonNull Response<IssNowResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateMap(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<IssNowResponse> call, @NonNull Throwable t) {
                // silent — next refresh in 5s
            }
        });
    }

    private void updateMap(IssNowResponse data) {
        if (googleMap == null) {
            return;
        }

        LatLng position = new LatLng(data.getLatitude(), data.getLongitude());

        positionHistory.add(position);
        if (positionHistory.size() > MAX_HISTORY_SIZE) {
            positionHistory.remove(0);
        }

        googleMap.clear();

        if (positionHistory.size() > 1) {
            googleMap.addPolyline(new PolylineOptions()
                    .addAll(positionHistory)
                    .width(5f)
                    .color(0xFF00C8FF));
        }

        googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title("ISS")
                .icon(issMarkerIcon));

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(position));

        updateInfoBar(data);
    }

    private void updateInfoBar(IssNowResponse data) {
        if (latTextView == null) {
            return;
        }
        latTextView.setText(String.format(Locale.US, "%.4f°", data.getLatitude()));
        lonTextView.setText(String.format(Locale.US, "%.4f°", data.getLongitude()));
        speedTextView.setText(String.format(Locale.US, "%.0f km/h", data.getVelocity()));
    }

    private BitmapDescriptor createIssMarkerIcon() {
        int size = (int) (56 * getResources().getDisplayMetrics().density);
        Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_iss_marker);
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, size, size);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
