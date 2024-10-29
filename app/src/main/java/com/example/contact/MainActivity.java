package com.example.contact;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private ListView listView;
    private SearchView searchView;
    private ArrayList<String> contactsList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.contacts_list_view);
        searchView = findViewById(R.id.search_view);
        contactsList = new ArrayList<>();

        // Vérifier et demander la permission d'accéder aux contacts
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            // Si la permission est déjà accordée, récupérer les contacts
            getContacts();
        }

        // Configurer la recherche
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        // Ajouter un clic pour partager le contact
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedContact = adapter.getItem(position);
                shareContact(selectedContact);
            }
        });
    }

    private void getContacts() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            // Utiliser un Set pour éviter les doublons
            Set<String> uniqueContacts = new HashSet<>();

            while (cursor.moveToNext()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                // Nettoyer le numéro de téléphone en supprimant les espaces, tirets, etc.
                String normalizedPhoneNumber = phoneNumber.replaceAll("[\\s\\-()]", "");

                // Créer une clé unique basée sur le nom et le numéro nettoyé
                String contactKey = name + ":" + normalizedPhoneNumber;

                // Ajouter le contact au Set pour éviter les répétitions
                if (uniqueContacts.add(contactKey)) {
                    // Si l'ajout a réussi, ajouter le contact à la liste des contacts à afficher
                    contactsList.add(name + ": " + phoneNumber);
                }
            }
            cursor.close();

            // Trier les contacts par ordre alphabétique
            Collections.sort(contactsList);

            // Afficher les contacts dans la ListView
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactsList);
            listView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "Aucun contact trouvé", Toast.LENGTH_SHORT).show();
        }
    }


    private void shareContact(String contact) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Contact: " + contact);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "Partager le contact via"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts();
            } else {
                Toast.makeText(this, "Permission refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
