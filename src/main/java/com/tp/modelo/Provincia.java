package com.tp.modelo;

public class Provincia {
    
    private int id;
    private String nombre;
    private Pais pais;

    public Provincia(String nombre, int id, Pais pais) {
        this.id = id;
        this.nombre = nombre;
        this.pais = pais;
    }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPais(Pais pais) { this.pais = pais; }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public Pais getPais() { return pais; }
}
