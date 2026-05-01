package dev.bodid.isstracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dev.bodid.isstracker.model.Astronaut;
import dev.bodid.isstracker.model.Nationality;

public class AstronautAdapter extends RecyclerView.Adapter<AstronautAdapter.ViewHolder> {

    private final List<Astronaut> astronauts;

    public AstronautAdapter(List<Astronaut> astronauts) {
        this.astronauts = astronauts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_astronaut, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(astronauts.get(position));
    }

    @Override
    public int getItemCount() {
        return astronauts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView profileImage;
        private final TextView nameTextView;
        private final TextView agencyTextView;
        private final TextView nationalityTextView;
        private final TextView flightsTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            agencyTextView = itemView.findViewById(R.id.agency_text_view);
            nationalityTextView = itemView.findViewById(R.id.nationality_text_view);
            flightsTextView = itemView.findViewById(R.id.flights_text_view);
        }

        void bind(Astronaut astronaut) {
            nameTextView.setText(astronaut.getName());

            if (astronaut.getAgency() != null) {
                agencyTextView.setText(astronaut.getAgency().getName());
            }

            List<Nationality> nationalities = astronaut.getNationality();
            if (nationalities != null && !nationalities.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < nationalities.size(); i++) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append(nationalities.get(i).getName());
                }
                nationalityTextView.setText(sb.toString());
            }

            flightsTextView.setText("Repülések: " + astronaut.getFlightsCount());

            String imageUrl = astronaut.getProfileImageThumbnail();
            if (imageUrl == null || imageUrl.isEmpty()) {
                imageUrl = astronaut.getProfileImage();
            }
            Glide.with(profileImage.getContext())
                    .load(imageUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_crew)
                    .error(R.drawable.ic_crew)
                    .into(profileImage);
        }
    }
}
