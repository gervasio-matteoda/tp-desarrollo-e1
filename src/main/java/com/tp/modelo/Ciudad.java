package com.tp.modelo;

public class Ciudad {

    private int id;
    private String nombre;
    private Provincia provincia;

    public Ciudad(String nombre, int id, Provincia provincia) {
        this.id = id;
        this.nombre = nombre;
        this.provincia = provincia;
    }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setProvincia(Provincia provincia) { this.provincia = provincia; }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public Provincia getProvincia() { return provincia; }
}
