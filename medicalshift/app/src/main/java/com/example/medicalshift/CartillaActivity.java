package com.example.medicalshift;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CartillaActivity extends AppCompatActivity {

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartilla);

        loadCurrentUser();

        RecyclerView recyclerCartillaOpciones = findViewById(R.id.recyclerCartillaOpciones);
        recyclerCartillaOpciones.setLayoutManager(new GridLayoutManager(this, 2));

        List<CartillaOpcion> opciones = new ArrayList<>();
        opciones.add(new CartillaOpcion("Especialidades Médicas", R.drawable.ic_stethoscope, "medicSpecialties.json", "providersByMedic.json"));
        opciones.add(new CartillaOpcion("Diagnóstico y Tratamiento", R.drawable.ic_microscope, "diagnosticSpecialties.json", "providersByDiagnostic.json"));
        opciones.add(new CartillaOpcion("Búsqueda por Profesional", R.drawable.ic_doctor, null, "professionals.json"));
        opciones.add(new CartillaOpcion("Servicio de Guardia", R.drawable.ic_ambulance, "urgencySpecialties.json", "providersByUrgency.json"));
        opciones.add(new CartillaOpcion("Servicio de Internación", R.drawable.ic_hospital, "inpatientSpecialties.json", "providersByInpatient.json"));
        opciones.add(new CartillaOpcion("Odontología", R.drawable.ic_tooth, "odontologySpecialties.json", "providersByOdontology.json"));
        opciones.add(new CartillaOpcion("Farmacias", R.drawable.ic_medicine, null, "providersByPharmacy.json"));
        opciones.add(new CartillaOpcion("Vacunatorios", R.drawable.ic_vaccine, null, "providersByVaccine.json"));

        OpcionAdapter adapter = new OpcionAdapter(opciones, this::onOpcionClick);
        recyclerCartillaOpciones.setAdapter(adapter);
    }

    private void onOpcionClick(CartillaOpcion opcion) {
        if (opcion.getNombre().equals("Farmacias") || opcion.getNombre().equals("Vacunatorios")) {
            Intent intent = new Intent(this, ResultsActivity.class);
            intent.putExtra(ResultsActivity.EXTRA_TITLE, opcion.getNombre());
            intent.putExtra(ResultsActivity.EXTRA_PROVIDERS_FILE, opcion.getJsonProvidersFile());
            intent.putExtra(ResultsActivity.EXTRA_LOCATION, currentUser.getLocalidad());
            startActivity(intent);
        } else if (opcion.getNombre().equals("Búsqueda por Profesional")) {
            showProfessionalSearchDialog(opcion);
        } else {
            showSearchDialog(opcion);
        }
    }

    private void showProfessionalSearchDialog(CartillaOpcion opcion) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_professional_search);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText etProfessionalName = dialog.findViewById(R.id.etProfessionalName);
        EditText etInstitutionName = dialog.findViewById(R.id.etInstitutionName);
        MaterialButton btnSearch = dialog.findViewById(R.id.btnSearchProfessional);

        btnSearch.setOnClickListener(v -> {
            String professionalName = etProfessionalName.getText().toString().trim();
            String institutionName = etInstitutionName.getText().toString().trim();

            Intent intent = new Intent(this, ResultsActivity.class);
            intent.putExtra(ResultsActivity.EXTRA_TITLE, "Resultados de Búsqueda");
            intent.putExtra(ResultsActivity.EXTRA_PROVIDERS_FILE, opcion.getJsonProvidersFile());
            intent.putExtra(ResultsActivity.EXTRA_LOCATION, currentUser.getLocalidad());
            intent.putExtra(ResultsActivity.EXTRA_PROFESSIONAL_NAME, professionalName);
            intent.putExtra(ResultsActivity.EXTRA_INSTITUTION_NAME, institutionName);
            startActivity(intent);

            dialog.dismiss();
        });

        dialog.show();
    }

    private void showSearchDialog(CartillaOpcion opcion) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_search);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
        Spinner spinnerSpecialties = dialog.findViewById(R.id.spinnerSpecialties);
        MaterialButton btnSearch = dialog.findViewById(R.id.btnSearch);
        
        dialog.findViewById(R.id.recyclerResults).setVisibility(View.GONE);
        dialog.findViewById(R.id.progressBar).setVisibility(View.GONE);

        dialogTitle.setText(opcion.getNombre());

        if (opcion.getJsonSpecialtiesFile() != null) {
            try {
                List<String> specialitiesList = getSpecialtiesFromJson(opcion.getJsonSpecialtiesFile());
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, specialitiesList);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSpecialties.setAdapter(spinnerAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar especialidades", Toast.LENGTH_SHORT).show();
            }
        } else {
            spinnerSpecialties.setVisibility(View.GONE);
        }

        btnSearch.setOnClickListener(v -> {
            String selectedSpecialty = "";
            if (spinnerSpecialties.getVisibility() == View.VISIBLE) {
                selectedSpecialty = spinnerSpecialties.getSelectedItem().toString();
            }

            Intent intent = new Intent(this, ResultsActivity.class);
            intent.putExtra(ResultsActivity.EXTRA_TITLE, opcion.getNombre());
            intent.putExtra(ResultsActivity.EXTRA_PROVIDERS_FILE, opcion.getJsonProvidersFile());
            intent.putExtra(ResultsActivity.EXTRA_LOCATION, currentUser.getLocalidad());
            intent.putExtra(ResultsActivity.EXTRA_SPECIALTY, selectedSpecialty);
            startActivity(intent);

            dialog.dismiss();
        });

        dialog.show();
    }

    private void loadCurrentUser() {
        try {
            String json = loadJSONFromAsset("users.json");
            if (json != null) {
                JSONArray usersArray = new JSONArray(json);
                if (usersArray.length() > 0) {
                    currentUser = new User(usersArray.getJSONObject(0));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<String> getSpecialtiesFromJson(String fileName) throws JSONException {
        List<String> list = new ArrayList<>();
        String json = loadJSONFromAsset(fileName);
        if (json != null) {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
        }
        return list;
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

    private interface OnOpcionClickListener {
        void onOpcionClick(CartillaOpcion opcion);
    }

    private static class CartillaOpcion {
        final String nombre; final int iconoResId; final String jsonSpecialtiesFile; final String jsonProvidersFile;
        public CartillaOpcion(String n, int i, String js, String jp) { this.nombre = n; this.iconoResId = i; this.jsonSpecialtiesFile = js; this.jsonProvidersFile = jp; }
        public String getNombre() { return nombre; }
        public String getJsonProvidersFile() { return jsonProvidersFile; }
        public String getJsonSpecialtiesFile() { return jsonSpecialtiesFile; }
    }
    
    private static class User {
        private final String plan; private final String localidad;
        public User(JSONObject object) throws JSONException { this.plan = object.getString("Plan"); this.localidad = object.getJSONObject("Domicilio de Residencia").getString("Localidad"); }
        public String getPlan() { return plan; }
        public String getLocalidad() { return localidad; }
    }
    
    private static class OpcionAdapter extends RecyclerView.Adapter<OpcionAdapter.ViewHolder> {
        private final List<CartillaOpcion> opciones; private final OnOpcionClickListener listener;
        public OpcionAdapter(List<CartillaOpcion> o, OnOpcionClickListener l) { this.opciones = o; this.listener = l; }

        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cartilla_opcion, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CartillaOpcion opcion = opciones.get(position);
            holder.nombreOpcion.setText(opcion.nombre);
            holder.iconOpcion.setImageResource(opcion.iconoResId);
            holder.itemView.setOnClickListener(v -> listener.onOpcionClick(opcion));
        }

        @Override
        public int getItemCount() { return opciones.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView iconOpcion; final TextView nombreOpcion;
            public ViewHolder(View view) {
                super(view);
                iconOpcion = view.findViewById(R.id.iconOpcion);
                nombreOpcion = view.findViewById(R.id.nombreOpcion);
            }
        }
    }
}