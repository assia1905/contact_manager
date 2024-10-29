package com.example.contact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ContactAccessActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_access);

        Button accessContactsButton = findViewById(R.id.access_contacts_button);
        accessContactsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ContactAccessActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
