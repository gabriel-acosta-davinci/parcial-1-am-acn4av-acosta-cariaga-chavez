package com.example.medicalshift;

public class Provider {
    final String name;
    final String address;
    final String specialty;
    final String phone;
    final String institution; // Nuevo campo

    public Provider(String name, String address, String specialty, String phone, String institution) {
        this.name = name;
        this.address = address;
        this.specialty = specialty;
        this.phone = phone;
        this.institution = institution;
    }
}
