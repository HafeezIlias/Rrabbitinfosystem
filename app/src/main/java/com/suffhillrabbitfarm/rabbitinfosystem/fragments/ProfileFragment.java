package com.suffhillrabbitfarm.rabbitinfosystem.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.suffhillrabbitfarm.rabbitinfosystem.R;
import com.suffhillrabbitfarm.rabbitinfosystem.data.ApiHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {

    TextView textViewRabbitId, textViewBreed, textViewColor, textViewWeight, textViewDOB;
    EditText editTextFatherId, editTextMotherId, editTextCageNumber;
    TextView textViewObservations, textViewGender;
    Button btnUpdateProfile;

    String currentRabbitId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        loadRabbitProfile();
        setupUpdateButton();

        return view;
    }

    private void initViews(View view) {
        // Try to find the views - some may not exist in the current layout
        try {
            textViewRabbitId = view.findViewById(R.id.rabbit_id_edit);
            textViewBreed = view.findViewById(R.id.breed_edit);
            textViewColor = view.findViewById(R.id.color_edit);
            textViewWeight = view.findViewById(R.id.weight_edit);
            textViewDOB = view.findViewById(R.id.dob_edit);
            editTextFatherId = view.findViewById(R.id.father_id_edit);
            editTextMotherId = view.findViewById(R.id.mother_id_edit);
            textViewObservations = view.findViewById(R.id.observations_edit);
            textViewGender = view.findViewById(R.id.gender_edit);
            editTextCageNumber = view.findViewById(R.id.cage_number_edit);
            btnUpdateProfile = view.findViewById(R.id.btn_update_profile);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Profile layout needs to be updated with proper view IDs", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void setupUpdateButton() {
        if (btnUpdateProfile != null) {
            btnUpdateProfile.setOnClickListener(v -> updateProfile());
        }
    }

    private void updateProfile() {
        try {
            if (currentRabbitId.isEmpty()) {
                Toast.makeText(getContext(), "Rabbit ID not found", Toast.LENGTH_SHORT).show();
                return;
            }

            String fatherId = editTextFatherId != null ? editTextFatherId.getText().toString().trim() : "";
            String motherId = editTextMotherId != null ? editTextMotherId.getText().toString().trim() : "";
            String cageNumber = editTextCageNumber != null ? editTextCageNumber.getText().toString().trim() : "";

            // Create update request
            JSONObject updateData = new JSONObject();
            updateData.put("rabbit_id", currentRabbitId);
            if (!fatherId.isEmpty())
                updateData.put("father_id", fatherId);
            if (!motherId.isEmpty())
                updateData.put("mother_id", motherId);
            if (!cageNumber.isEmpty())
                updateData.put("cage_number", cageNumber);

            // For now, show a message about the update
            Toast.makeText(getContext(), "Profile update feature needs API integration", Toast.LENGTH_LONG).show();

            // TODO: Implement API call to update rabbit profile
            // ApiHelper.updateRabbitProfile(updateData, callback);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadRabbitProfile() {
        // Get rabbit data from bundle
        String rabbitDataJson = getArguments() != null ? getArguments().getString("RabbitData") : null;
        String rabbitId = getArguments() != null ? getArguments().getString("RabbitID") : "";

        if (rabbitDataJson != null && !rabbitDataJson.isEmpty()) {
            // Parse and display actual rabbit data
            try {
                JSONObject rabbitData = new JSONObject(rabbitDataJson);

                String id = rabbitData.optString("rabbit_id", "Unknown");
                String breed = rabbitData.optString("breed", "Not specified");
                String color = rabbitData.optString("color", "Not specified");
                String gender = rabbitData.optString("gender", "Not specified");
                String weight = rabbitData.optString("weight", "Not recorded");
                String dob = rabbitData.optString("dob", "Not specified");
                String fatherId = rabbitData.optString("father_id", "Not specified");
                String motherId = rabbitData.optString("mother_id", "Not specified");
                String observations = rabbitData.optString("observations", "None");
                String cageNumber = rabbitData.optString("cage_number", "Not assigned");

                // Format weight if it's a number
                if (!weight.equals("Not recorded") && !weight.isEmpty()) {
                    try {
                        double w = Double.parseDouble(weight);
                        weight = String.format("%.2f kg", w);
                    } catch (NumberFormatException e) {
                        weight = weight + " kg";
                    }
                }

                setProfileText(
                        "ID: " + id,
                        "Breed: " + breed,
                        "Color: " + color,
                        "Weight: " + weight,
                        "DOB: " + dob,
                        fatherId,
                        motherId,
                        "Observations: " + observations,
                        "Gender: " + gender,
                        cageNumber);

                currentRabbitId = id;

            } catch (JSONException e) {
                Toast.makeText(getContext(), "Error parsing rabbit data", Toast.LENGTH_SHORT).show();
                setDefaultText();
            }
        } else {
            // Fallback to basic info if no detailed data available
            setProfileText(
                    "ID: " + rabbitId,
                    "Breed: Data not available",
                    "Color: Data not available",
                    "Weight: Data not available",
                    "DOB: Data not available",
                    "", // Empty values for EditText fields
                    "",
                    "Observations: Data not available",
                    "Gender: Data not available",
                    "");
        }
    }

    private void setDefaultText() {
        setProfileText("ID: Unknown", "Breed: Not available", "Color: Not available",
                "Weight: Not available", "DOB: Not available", "",
                "", "Observations: Not available", "Gender: Not available", "");
    }

    private void setProfileText(String id, String breed, String color, String weight,
            String dob, String father, String mother, String observations, String gender, String cage) {
        try {
            if (textViewRabbitId != null)
                textViewRabbitId.setText(id);
            if (textViewBreed != null)
                textViewBreed.setText(breed);
            if (textViewColor != null)
                textViewColor.setText(color);
            if (textViewWeight != null)
                textViewWeight.setText(weight);
            if (textViewDOB != null)
                textViewDOB.setText(dob);
            if (editTextFatherId != null)
                editTextFatherId.setText(father);
            if (editTextMotherId != null)
                editTextMotherId.setText(mother);
            if (textViewObservations != null)
                textViewObservations.setText(observations);
            if (textViewGender != null)
                textViewGender.setText(gender);
            if (editTextCageNumber != null)
                editTextCageNumber.setText(cage);
        } catch (Exception e) {
            // Handle case where views don't exist
        }
    }
}