package com.example.contact;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Lancer l'animation ici (fade in, zoom, etc.)
        ImageView welcomeImage = findViewById(R.id.welcome_image);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        welcomeImage.startAnimation(fadeInAnimation);


        // Délai avant de passer à la deuxième activité
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(WelcomeActivity.this, ContactAccessActivity.class);
            startActivity(intent);
            finish();
        }, 3000); // 3 secondes de délai
    }
}
