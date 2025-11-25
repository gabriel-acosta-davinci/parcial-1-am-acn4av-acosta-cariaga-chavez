package com.example.medicalshift;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfesionalAdapter extends RecyclerView.Adapter<ProfesionalAdapter.ViewHolder> {

    private List<Profesional> listaMostrada;
    private final List<Profesional> listaCompleta;

    public ProfesionalAdapter(List<Profesional> listaProfesionales) {
        this.listaCompleta = new ArrayList<>(listaProfesionales);
        this.listaMostrada = new ArrayList<>();
        mostrarOriginales();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profesional, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Profesional profesional = listaMostrada.get(position);
        holder.nombre.setText(profesional.getNombre());
        holder.especialidad.setText(profesional.getEspecialidad());
    }

    @Override
    public int getItemCount() {
        return listaMostrada.size();
    }

    public void filtrar(String texto) {
        listaMostrada.clear();
        if (texto.isEmpty()) {
            mostrarOriginales();
        } else {
            String textoBusqueda = texto.toLowerCase(Locale.getDefault());
            for (Profesional profesional : listaCompleta) {
                if (profesional.getNombre().toLowerCase(Locale.getDefault()).contains(textoBusqueda)) {
                    listaMostrada.add(profesional);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void mostrarOriginales() {
        listaMostrada.clear();
        for (int i = 0; i < Math.min(3, listaCompleta.size()); i++) {
            listaMostrada.add(listaCompleta.get(i));
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        TextView especialidad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // IDs Corregidos
            nombre = itemView.findViewById(R.id.nombreProfesional); 
            especialidad = itemView.findViewById(R.id.especialidadProfesional); 
        }
    }
}
