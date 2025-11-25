package com.example.medicalshift;

import org.json.JSONException;
import org.json.JSONObject;

public class Profesional {
    private final String nombre;
    private final String especialidad;
    private final String institucion;
    private final String direccion;
    private final String localidad;
    private final String telefono;

    public Profesional(JSONObject object) throws JSONException {
        this.nombre = object.getString("nombre");
        this.especialidad = object.getString("especialidad");
        this.institucion = object.getString("institucion");
        this.direccion = object.getString("direccion");
        this.localidad = object.getString("localidad");
        this.telefono = object.getString("telefono");
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getEspecialidad() { return especialidad; }
}
