package com.example.medicalshift;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ResultsActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_PROVIDERS_FILE = "EXTRA_PROVIDERS_FILE";
    public static final String EXTRA_LOCATION = "EXTRA_LOCATION";
    public static final String EXTRA_SPECIALTY = "EXTRA_SPECIALTY";
    public static final String EXTRA_PROFESSIONAL_NAME = "EXTRA_PROFESSIONAL_NAME";
    public static final String EXTRA_INSTITUTION_NAME = "EXTRA_INSTITUTION_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        TextView titleView = findViewById(R.id.resultsTitle);
        ProgressBar progressBar = findViewById(R.id.resultsProgressBar);
        RecyclerView recyclerView = findViewById(R.id.recyclerResultsView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String providersFile = getIntent().getStringExtra(EXTRA_PROVIDERS_FILE);
        String userLocation = getIntent().getStringExtra(EXTRA_LOCATION);
        String specialty = getIntent().getStringExtra(EXTRA_SPECIALTY);
        String professionalName = getIntent().getStringExtra(EXTRA_PROFESSIONAL_NAME);
        String institutionName = getIntent().getStringExtra(EXTRA_INSTITUTION_NAME);

        titleView.setText(title);
        progressBar.setVisibility(View.VISIBLE);

        try {
            List<Provider> results;
            // Decidir qué lógica de búsqueda usar
            if (providersFile.equals("professionals.json")) {
                results = findProfessionals(providersFile, userLocation, professionalName, institutionName);
            } else if (specialty != null && !specialty.isEmpty()) {
                results = findProviders(providersFile, specialty, userLocation);
            } else {
                results = findProvidersByLocation(providersFile, userLocation);
            }

            ProviderAdapter adapter = new ProviderAdapter(results);
            recyclerView.setAdapter(adapter);

            progressBar.setVisibility(View.GONE);

            if (results.isEmpty()) {
                Toast.makeText(this, "No se encontraron resultados", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error al buscar proveedores", Toast.LENGTH_LONG).show();
        }
    }

    private List<Provider> findProfessionals(String fileName, String userLocation, String professionalName, String institutionName) throws JSONException {
        List<Provider> results = new ArrayList<>();
        String json = loadJSONFromAsset(fileName);
        if (json == null) return results;

        JSONArray professionalsArray = new JSONArray(json);
        for (int i = 0; i < professionalsArray.length(); i++) {
            JSONObject profObject = professionalsArray.getJSONObject(i);

            // 1. Filtro por Localidad (obligatorio)
            if (!profObject.getString("localidad").equalsIgnoreCase(userLocation)) {
                continue;
            }

            // 2. Filtro por Nombre de Profesional (opcional)
            if (professionalName != null && !professionalName.isEmpty()) {
                if (!profObject.getString("nombre").toLowerCase(Locale.ROOT).contains(professionalName.toLowerCase(Locale.ROOT))) {
                    continue;
                }
            }

            // 3. Filtro por Nombre de Institución (opcional)
            if (institutionName != null && !institutionName.isEmpty()) {
                if (!profObject.getString("institucion").toLowerCase(Locale.ROOT).contains(institutionName.toLowerCase(Locale.ROOT))) {
                    continue;
                }
            }

            // Si pasó los filtros, se agrega a la lista
            String name = profObject.getString("nombre");
            String specialty = profObject.getString("especialidad");
            String institution = profObject.getString("institucion");
            String address = profObject.getString("direccion");
            String phone = profObject.optString("telefono", "");

            results.add(new Provider(name, address, specialty, phone, institution));
        }
        return results;
    }

    private List<Provider> findProviders(String fileName, String specialty, String userLocation) throws JSONException {
        List<Provider> results = new ArrayList<>();
        String json = loadJSONFromAsset(fileName);
        if (json == null) return results;

        JSONObject root = new JSONObject(json);

        if (root.has(userLocation)) {
            JSONObject locationObject = root.getJSONObject(userLocation);

            if (locationObject.has(specialty)) {
                JSONArray providersArray = locationObject.getJSONArray(specialty);
                for (int i = 0; i < providersArray.length(); i++) {
                    JSONObject providerObject = providersArray.getJSONObject(i);
                    String name = providerObject.getString("nombre");
                    String address = providerObject.getString("direccion");
                    String phone = providerObject.optString("telefono", "");
                    results.add(new Provider(name, address, specialty, phone, name)); // Using name as institution for now
                }
            }
        }
        return results;
    }

    private List<Provider> findProvidersByLocation(String fileName, String userLocation) throws JSONException {
        List<Provider> results = new ArrayList<>();
        String json = loadJSONFromAsset(fileName);
        if (json == null) return results;

        JSONObject root = new JSONObject(json);

        if (root.has(userLocation)) {
            JSONArray providersArray = root.getJSONArray(userLocation);
            for (int i = 0; i < providersArray.length(); i++) {
                JSONObject providerObject = providersArray.getJSONObject(i);
                String name = providerObject.getString("nombre");
                String address = providerObject.getString("direccion");
                String phone = providerObject.optString("telefono", "");
                results.add(new Provider(name, address, "", phone, name)); // Using name as institution for now
            }
        }

        return results;
    }

    private String loadJSONFromAsset(String fileName) {
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
