package com.example.medicalshift;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InicioFragment extends Fragment {

    private OnNavigationRequest callback;
    private ProfesionalAdapter profesionalAdapter;
    private final List<Profesional> listaCompletaProfesionales = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnNavigationRequest) {
            callback = (OnNavigationRequest) context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Saludo (CORREGIDO) ---
        // El TextView ahora se busca dentro de la vista inflada del fragmento.
        TextView saludoHora = view.findViewById(R.id.saludoHora);
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        String mensaje;
        if (hora >= 6 && hora < 12) { mensaje = "Que tengas una excelente mañana ☀️";
        } else if (hora >= 12 && hora < 18) { mensaje = "¡Buena tarde! 🌤️";
        } else if (hora >= 18 && hora < 22) { mensaje = "Disfrutá tu noche 🌙";
        } else { mensaje = "Es hora de descansar 😴"; }
        saludoHora.setText(mensaje);

        // --- RecyclerView de trámites (sin cambios) ---
        RecyclerView recyclerTramites = view.findViewById(R.id.recyclerTramites);
        recyclerTramites.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Tramite> listaTramites = new ArrayList<>();
        listaTramites.add(new Tramite("Autorización médica", "28/09/2025", "Aprobado"));
        listaTramites.add(new Tramite("Pedido de credencial", "25/09/2025", "Pendiente"));
        listaTramites.add(new Tramite("Consulta virtual", "20/09/2025", "Rechazado"));
        TramiteAdapter tramiteAdapter = new TramiteAdapter(listaTramites);
        recyclerTramites.setAdapter(tramiteAdapter);

        // --- Búsqueda y RecyclerView de cartilla médica (LÓGICA REAL) ---
        loadProfesionales(); // Cargar datos del JSON

        RecyclerView recyclerCartilla = view.findViewById(R.id.recyclerCartilla);
        recyclerCartilla.setLayoutManager(new LinearLayoutManager(getContext()));
        
        profesionalAdapter = new ProfesionalAdapter(listaCompletaProfesionales);
        recyclerCartilla.setAdapter(profesionalAdapter);

        EditText searchCartilla = view.findViewById(R.id.searchCartilla);
        searchCartilla.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (profesionalAdapter != null) {
                    profesionalAdapter.filtrar(s.toString());
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        // --- Botones (sin cambios) ---
        Button btnVerTramites = view.findViewById(R.id.btnVerTramites);
        btnVerTramites.setOnClickListener(v -> {
            if (callback != null) callback.navegarA(2);
        });

        Button btnVerCartilla = view.findViewById(R.id.btnVerCartilla);
        btnVerCartilla.setOnClickListener(v -> {
            if (callback != null) callback.navegarA(1);
        });
    }

    private void loadProfesionales() {
        String json = loadJSONFromAsset("professionals.json");
        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    listaCompletaProfesionales.add(new Profesional(jsonArray.getJSONObject(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String loadJSONFromAsset(String fileName) {
        if (getContext() == null) return null;
        String json;
        try {
            InputStream is = getContext().getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public interface OnNavigationRequest {
        void navegarA(int posicion);
    }
}
