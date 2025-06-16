package com.suffhillrabbitfarm.rabbitinfosystem.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.suffhillrabbitfarm.rabbitinfosystem.R;

public class ProfileFragment extends Fragment {

    TextView textViewRabbitId, textViewBreed, textViewColor, textViewWeight, textViewDOB;
    TextView textViewFatherId, textViewMotherId, textViewObservations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        loadRabbitProfile();

        return view;
    }

    private void initViews(View view) {
        textViewRabbitId = view.findViewById(R.id.rabbit_id_edit);
        textViewBreed = view.findViewById(R.id.breed_edit);
        textViewColor = view.findViewById(R.id.color_edit);
        textViewWeight = view.findViewById(R.id.weight_edit);
        textViewDOB = view.findViewById(R.id.dob_edit);
        textViewFatherId = view.findViewById(R.id.father_id_edit);
        textViewMotherId = view.findViewById(R.id.mother_id_edit);
        textViewObservations = view.findViewById(R.id.observations_edit);
    }

    private void loadRabbitProfile() {
        String rabbitId = getArguments() != null ? getArguments().getString("rabbitId") : "";

        if (rabbitId.isEmpty()) {
            textViewRabbitId.setText("ID: Unknown");
            textViewBreed.setText("Breed: Not available");
            textViewColor.setText("Color: Not available");
            textViewWeight.setText("Weight: Not available");
            textViewDOB.setText("DOB: Not available");
            textViewFatherId.setText("Father: Not available");
            textViewMotherId.setText("Mother: Not available");
            textViewObservations.setText("Observations: Not available");
            return;
        }

        // For now, we'll display the rabbit ID and show a message that
        // we need to get rabbit details from the get_rabbits API
        textViewRabbitId.setText("ID: " + rabbitId);
        textViewBreed.setText("Breed: Loading...");
        textViewColor.setText("Color: Loading...");
        textViewWeight.setText("Weight: Loading...");
        textViewDOB.setText("DOB: Loading...");
        textViewFatherId.setText("Father: Loading...");
        textViewMotherId.setText("Mother: Loading...");
        textViewObservations.setText("Observations: Loading...");

        // Note: The current API structure doesn't have a get single rabbit endpoint
        // We would need to either:
        // 1. Add a new API endpoint for getting single rabbit details
        // 2. Use the get_rabbits endpoint and filter by rabbit_id
        // 3. Pass the rabbit data from the parent activity

        // For now, showing a message about this limitation
        Toast.makeText(getContext(),
                "Profile loading requires either passing rabbit data from parent or creating a get_single_rabbit API endpoint",
                Toast.LENGTH_LONG).show();
    }
}