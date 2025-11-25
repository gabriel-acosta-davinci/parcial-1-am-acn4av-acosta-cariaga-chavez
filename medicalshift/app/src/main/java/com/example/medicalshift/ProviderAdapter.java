package com.example.medicalshift;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ViewHolder> {
    private final List<Provider> providers;

    public ProviderAdapter(List<Provider> providers) {
        this.providers = providers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_provider, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Provider provider = providers.get(position);
        holder.providerName.setText(provider.name);
        
        // Mostrar Institución si existe
        if (provider.institution != null && !provider.institution.isEmpty()) {
            holder.providerInstitution.setText(provider.institution);
            holder.providerInstitution.setVisibility(View.VISIBLE);
        } else {
            holder.providerInstitution.setVisibility(View.GONE);
        }

        // Mostrar Especialidad si existe
        if (provider.specialty != null && !provider.specialty.isEmpty()) {
            holder.providerSpecialty.setText(provider.specialty);
            holder.providerSpecialty.setVisibility(View.VISIBLE);
        } else {
            holder.providerSpecialty.setVisibility(View.GONE);
        }

        holder.providerAddress.setText(provider.address);

        // Mostrar Teléfono si existe
        if (provider.phone != null && !provider.phone.isEmpty()) {
            holder.providerPhone.setText(provider.phone);
            holder.providerPhone.setVisibility(View.VISIBLE);
        } else {
            holder.providerPhone.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return providers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView providerName, providerInstitution, providerSpecialty, providerAddress, providerPhone;

        public ViewHolder(View view) {
            super(view);
            providerName = view.findViewById(R.id.providerName);
            providerInstitution = view.findViewById(R.id.providerInstitution);
            providerSpecialty = view.findViewById(R.id.providerSpecialty);
            providerAddress = view.findViewById(R.id.providerAddress);
            providerPhone = view.findViewById(R.id.providerPhone);
        }
    }
}
