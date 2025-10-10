package com.tp.modelo;

public class Pais {
    
    private int id;
    private String nombre;

    public Pais(String nombre, int id) {
        this.id = id;
        this.nombre = nombre;
    }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
}
