package com.example.smartcents;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SavingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.savings_fragment_container, new SavingsFragment())
                    .commit();
        }
    }
}
