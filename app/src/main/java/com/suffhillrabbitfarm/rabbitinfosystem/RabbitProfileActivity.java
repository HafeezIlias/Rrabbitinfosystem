package com.suffhillrabbitfarm.rabbitinfosystem;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.suffhillrabbitfarm.rabbitinfosystem.data.ApiHelper;
import com.suffhillrabbitfarm.rabbitinfosystem.data.LoginData;
import com.suffhillrabbitfarm.rabbitinfosystem.helpers.MiscHelper;
import com.suffhillrabbitfarm.rabbitinfosystem.models.RabbitModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RabbitProfileActivity extends AppCompatActivity {

    TextInputEditText editTextRabbitID, editTextColor, editTextWeight,
            editTextFatherID, editTextMotherID, editTextObservations, editTextDOB;
    AutoCompleteTextView editTextBreed;
    RadioGroup genderRadioGroup;
    FloatingActionButton buttonSaveProfile;
    Toolbar toolbar;
    String dateOfBirth;
    LoginData loginData;
    List<String> breedsList;
    ArrayAdapter<String> breedsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rabbit_profile);

        initData();
        initViews();
        setupToolbar();
        loadBreeds();
    }

    private void initData() {
        loginData = new LoginData(this);
        breedsList = new ArrayList<>();
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Handle back button click
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void initViews() {
        editTextRabbitID = findViewById(R.id.rabbit_id_edit);
        editTextBreed = findViewById(R.id.breed_edit);
        editTextColor = findViewById(R.id.color_edit);
        editTextWeight = findViewById(R.id.weight_edit);
        editTextFatherID = findViewById(R.id.father_id_edit);
        editTextMotherID = findViewById(R.id.mother_id_edit);
        editTextObservations = findViewById(R.id.observations_edit);
        editTextDOB = findViewById(R.id.dob_edit);
        genderRadioGroup = findViewById(R.id.gender_radio_group);
        buttonSaveProfile = findViewById(R.id.save_button);

        // Set up date picker for DOB
        editTextDOB.setOnClickListener(view -> showDatePicker());
        editTextDOB.setFocusable(false);

        // Set up save button
        buttonSaveProfile.setOnClickListener(view -> validateAndSaveProfile());

        // Set default gender selection to female
        genderRadioGroup.check(R.id.radio_female);

        // Setup breed dropdown
        breedsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, breedsList);
        editTextBreed.setAdapter(breedsAdapter);
    }

    private void loadBreeds() {
        ApiHelper.getRabbitBreeds(new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray breedsArray = response.getJSONArray("breeds");
                    breedsList.clear();

                    for (int i = 0; i < breedsArray.length(); i++) {
                        JSONObject breed = breedsArray.getJSONObject(i);
                        String breedName = breed.getString("breed_name");
                        breedsList.add(breedName);
                    }

                    // Update adapter
                    breedsAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Toast.makeText(RabbitProfileActivity.this, "Error loading breeds: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(RabbitProfileActivity.this, "Failed to load breeds: " + error,
                        Toast.LENGTH_SHORT).show();

                // Add some default breeds as fallback
                breedsList.clear();
                breedsList.add("New Zealand White");
                breedsList.add("California");
                breedsList.add("Dutch");
                breedsList.add("Flemish Giant");
                breedsList.add("Rex");
                breedsList.add("Mini Lop");
                breedsList.add("Angora");
                breedsList.add("Chinchilla");
                breedsAdapter.notifyDataSetChanged();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format date as YYYY-MM-DD
                    dateOfBirth = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    editTextDOB.setText(dateOfBirth);
                }, year, month, day);

        // Set max date to today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private String getSelectedGender() {
        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.radio_male) {
            return "male";
        } else if (selectedId == R.id.radio_female) {
            return "female";
        }
        return "female"; // Default to female if nothing selected
    }

    private void validateAndSaveProfile() {
        RabbitModel rabbit = new RabbitModel();
        rabbit.setRabbit_id(editTextRabbitID.getText().toString().trim());
        rabbit.setBreed(editTextBreed.getText().toString().trim());
        rabbit.setColor(editTextColor.getText().toString().trim());
        rabbit.setWeight(editTextWeight.getText().toString().trim());
        rabbit.setFather_id(editTextFatherID.getText().toString().trim());
        rabbit.setMother_id(editTextMotherID.getText().toString().trim());
        rabbit.setObservations(editTextObservations.getText().toString().trim());
        rabbit.setDob(dateOfBirth);

        // Validation
        if (rabbit.getRabbit_id().isEmpty()) {
            editTextRabbitID.setError("Rabbit ID Required");
            editTextRabbitID.requestFocus();
            return;
        }
        if (rabbit.getBreed().isEmpty()) {
            editTextBreed.setError("Breed Required");
            editTextBreed.requestFocus();
            return;
        }
        if (rabbit.getColor().isEmpty()) {
            editTextColor.setError("Color Required");
            editTextColor.requestFocus();
            return;
        }
        if (rabbit.getWeight().isEmpty()) {
            editTextWeight.setError("Weight Required");
            editTextWeight.requestFocus();
            return;
        }
        if (dateOfBirth == null || dateOfBirth.isEmpty()) {
            editTextDOB.setError("Date of Birth Required");
            editTextDOB.requestFocus();
            Toast.makeText(this, "Please select date of birth", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate gender selection
        if (genderRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return;
        }

        saveRabbitProfile(rabbit);
    }

    private void saveRabbitProfile(RabbitModel rabbit) {
        String userUid = loginData.getUserUid();
        if (userUid.isEmpty()) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Dialog dialog = MiscHelper.openNetLoaderDialog(this);

        // Convert weight string to double
        Double weightValue = null;
        if (!rabbit.getWeight().isEmpty()) {
            try {
                weightValue = Double.parseDouble(rabbit.getWeight());
            } catch (NumberFormatException e) {
                dialog.dismiss();
                editTextWeight.setError("Invalid weight format");
                editTextWeight.requestFocus();
                Toast.makeText(this, "Invalid weight format", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Get selected gender
        String gender = getSelectedGender();

        ApiHelper.addRabbit(
                userUid,
                rabbit.getRabbit_id(),
                rabbit.getBreed(),
                rabbit.getColor(),
                gender,
                weightValue,
                rabbit.getDob(),
                rabbit.getFather_id(),
                rabbit.getMother_id(),
                rabbit.getObservations(),
                new ApiHelper.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        dialog.dismiss();
                        Toast.makeText(RabbitProfileActivity.this, "Rabbit profile saved successfully",
                                Toast.LENGTH_SHORT).show();

                        // Return to previous activity
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        dialog.dismiss();
                        Toast.makeText(RabbitProfileActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}