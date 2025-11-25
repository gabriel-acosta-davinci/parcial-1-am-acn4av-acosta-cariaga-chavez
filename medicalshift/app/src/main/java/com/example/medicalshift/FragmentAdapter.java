package com.example.medicalshift;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {

    public FragmentAdapter(@NonNull FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new InicioFragment();
            case 1: return new CartillaFragment();
            case 2: return new TramitesFragment();
            case 3: return new PerfilFragment();
            default: return new InicioFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
