package com.example.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Find the ImageView
        ImageView logoImageView = findViewById(R.id.logoImageView);

        // Load the fade animation
        Animation fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_animation);

        // Start the animation
        logoImageView.startAnimation(fadeAnimation);

        // Set a delay to transition to the next activity (after splash)
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close SplashActivity
        }, 3000); // Delay of 3 seconds

        // Hide the ActionBar if it exists, since splash screens don't need it
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}
