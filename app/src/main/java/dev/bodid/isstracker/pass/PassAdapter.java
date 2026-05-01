package dev.bodid.isstracker.pass;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dev.bodid.isstracker.R;
import dev.bodid.isstracker.pass.model.SatellitePass;

public class PassAdapter extends RecyclerView.Adapter<PassAdapter.ViewHolder> {

    private final List<SatellitePass> passes;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd. HH:mm", Locale.getDefault());

    public PassAdapter(List<SatellitePass> passes) {
        this.passes = passes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pass, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(passes.get(position));
    }

    @Override
    public int getItemCount() {
        return passes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView timeTextView;
        private final TextView elevationTextView;
        private final TextView durationTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.pass_time_text_view);
            elevationTextView = itemView.findViewById(R.id.pass_elevation_text_view);
            durationTextView = itemView.findViewById(R.id.pass_duration_text_view);
        }

        void bind(SatellitePass pass) {
            String time = dateFormat.format(new Date(pass.getStartUTC() * 1000L));
            timeTextView.setText(time);
            elevationTextView.setText(String.format(Locale.getDefault(), "Max magasság: %.0f° (%s)", pass.getMaxEl(), pass.getMaxAzCompass()));
            durationTextView.setText(String.format(Locale.getDefault(), "Időtartam: %d mp", pass.getDuration()));
        }
    }
}
