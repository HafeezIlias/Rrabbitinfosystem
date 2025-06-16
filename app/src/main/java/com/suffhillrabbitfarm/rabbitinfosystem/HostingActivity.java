package com.suffhillrabbitfarm.rabbitinfosystem;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.suffhillrabbitfarm.rabbitinfosystem.fragments.HealthAndGrowthFragment;
import com.suffhillrabbitfarm.rabbitinfosystem.fragments.MatingFragment;
import com.suffhillrabbitfarm.rabbitinfosystem.fragments.ProfileFragment;
import com.suffhillrabbitfarm.rabbitinfosystem.fragments.WeightRecordFragment;

public class HostingActivity extends AppCompatActivity {
    TextView textViewRabbitID;
    String id;
    String type;
    String rabbitData;
    int id1, id4, id2, id3;
    FrameLayout frameLayout, frameLayout1, frameLayout2, frameLayout3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hosting);

        try {
            id = getIntent().getStringExtra("RabbitID");
            type = getIntent().getStringExtra("type");
            rabbitData = getIntent().getStringExtra("RabbitData");

            // Validate required data
            if (id == null || id.isEmpty()) {
                Toast.makeText(this, "Rabbit ID is required", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            if (type == null || type.isEmpty()) {
                type = "Rabbit Full Details"; // Default type
            }

            initViews();
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing activity: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            android.util.Log.e("HostingActivity", "Error in onCreate: " + e.getMessage(), e);
            finish();
        }
    }

    private void initViews() {
        textViewRabbitID = findViewById(R.id.textViewRabbitID);
        textViewRabbitID.setText(id);
        id1 = R.id.frameLayout;
        id2 = R.id.frameLayout1;
        id3 = R.id.frameLayout2;
        id4 = R.id.frameLayout3;
        frameLayout = findViewById(R.id.frameLayout);
        frameLayout1 = findViewById(R.id.frameLayout1);
        frameLayout2 = findViewById(R.id.frameLayout2);
        frameLayout3 = findViewById(R.id.frameLayout3);
        if (type.equals("Mating Information")) {
            Fragment fragment = new MatingFragment();
            LoadFragment(fragment, id1);
        } else if (type.equals("Health And Growth")) {
            Fragment fragment = new HealthAndGrowthFragment();
            LoadFragment(fragment, id1);
        } else if (type.equals("Weight History")) {
            Fragment fragment = new WeightRecordFragment();
            LoadFragment(fragment, id1);
        } else {
            // frameLayout.setVisibility(View.VISIBLE);
            // frameLayout1.setVisibility(View.VISIBLE);
            // frameLayout2.setVisibility(View.VISIBLE);
            // frameLayout3.setVisibility(View.VISIBLE);
            LoadFragment(new ProfileFragment(), id1);
            LoadFragment(new MatingFragment(), id2);
            LoadFragment(new HealthAndGrowthFragment(), id3);
            LoadFragment(new WeightRecordFragment(), id4);
        }
    }

    private void LoadFragment(Fragment fragment, int frameLayout) {
        Bundle bundle = new Bundle();
        bundle.putString("RabbitID", id);
        bundle.putString("type", type);

        if (rabbitData != null && !rabbitData.isEmpty()) {
            bundle.putString("RabbitData", rabbitData);
        }

        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(frameLayout, fragment)
                .commit();
    }
}