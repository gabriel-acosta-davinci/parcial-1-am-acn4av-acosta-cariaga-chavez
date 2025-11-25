package com.example.medicalshift;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class CartillaFragment extends Fragment {

    private User currentUser; // Para guardar los datos del usuario logueado

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cartilla, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadCurrentUser(); // Cargamos el usuario actual al crear la vista

        RecyclerView recyclerMasBuscados = view.findViewById(R.id.recyclerMasBuscados);
        recyclerMasBuscados.setLayoutManager(new LinearLayoutManager(getContext()));

        // CORRECCIÓN: Nombres de especialidades sin tildes para que coincidan con el JSON
        List<Especialidad> especialidades = new ArrayList<>();
        especialidades.add(new Especialidad("CLINICA MEDICA", "Búsqueda por profesional"));
        especialidades.add(new Especialidad("CARDIOLOGIA", "Búsqueda por profesional"));
        especialidades.add(new Especialidad("PEDIATRIA", "Búsqueda por profesional"));
        especialidades.add(new Especialidad("GINECOLOGIA", "Búsqueda por profesional"));
        especialidades.add(new Especialidad("TRAUMATOLOGIA", "Búsqueda por profesional"));

        EspecialidadAdapter adapter = new EspecialidadAdapter(especialidades, especialidad -> {
            if (currentUser == null) {
                Toast.makeText(getContext(), "No se pudieron cargar los datos del usuario.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getActivity(), ResultsActivity.class);
            
            intent.putExtra(ResultsActivity.EXTRA_TITLE, especialidad.getNombre());
            intent.putExtra(ResultsActivity.EXTRA_PROVIDERS_FILE, "providersByMedic.json");
            intent.putExtra(ResultsActivity.EXTRA_LOCATION, currentUser.getLocalidad());
            intent.putExtra(ResultsActivity.EXTRA_SPECIALTY, especialidad.getNombre());
            
            startActivity(intent);
        });

        recyclerMasBuscados.setAdapter(adapter);

        MaterialButton btnNuevaBusqueda = view.findViewById(R.id.btnNuevaBusqueda);
        btnNuevaBusqueda.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CartillaActivity.class);
            startActivity(intent);
        });
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
            if (getContext() != null) {
                Toast.makeText(getContext(), "Error al cargar datos de usuario", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String loadJSONFromAsset(String fileName) {
        if (getContext() == null) return null;
        try (InputStream is = getContext().getAssets().open(fileName)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // --- CLASES INTERNAS Y ADAPTADOR ---

    private interface OnEspecialidadClickListener {
        void onEspecialidadClick(Especialidad especialidad);
    }

    private static class User {
        private final String localidad;
        public User(JSONObject object) throws JSONException {
            this.localidad = object.getJSONObject("Domicilio de Residencia").getString("Localidad");
        }
        public String getLocalidad() { return localidad; }
    }

    private static class Especialidad {
        private final String nombre;
        private final String tipoSolicitud;

        public Especialidad(String nombre, String tipoSolicitud) {
            this.nombre = nombre;
            this.tipoSolicitud = tipoSolicitud;
        }
        public String getNombre() { return nombre; }
    }

    private static class EspecialidadAdapter extends RecyclerView.Adapter<EspecialidadAdapter.ViewHolder> {
        private final List<Especialidad> especialidades;
        private final OnEspecialidadClickListener listener;

        public EspecialidadAdapter(List<Especialidad> especialidades, OnEspecialidadClickListener listener) {
            this.especialidades = especialidades;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_especialidad_buscada, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Especialidad especialidad = especialidades.get(position);
            holder.nombreEspecialidad.setText(especialidad.getNombre());
            holder.tipoSolicitud.setText(especialidad.tipoSolicitud);
            holder.itemView.setOnClickListener(v -> listener.onEspecialidadClick(especialidad));
        }

        @Override
        public int getItemCount() {
            return especialidades.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final TextView nombreEspecialidad, tipoSolicitud;
            public ViewHolder(View view) {
                super(view);
                nombreEspecialidad = view.findViewById(R.id.nombreEspecialidad);
                tipoSolicitud = view.findViewById(R.id.tipoSolicitud);
            }
        }
    }
}
