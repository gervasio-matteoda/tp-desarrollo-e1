package com.tp.modelo;

public class Provincia {
    
    private int id;
    private String nombre;
    private Pais pais;

    private Provincia(Builder builder) { // Constructor privado
        this.id = builder.id;
        this.nombre = builder.nombre;
        this.pais = builder.pais;
    }

    // Constructor anterior
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

    // Builder
    public static class Builder {
        private int id;
        private String nombre;
        private Pais pais;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public Builder pais(Pais pais) {
            this.pais = pais;
            return this;
        }

        public Provincia build() {
            return new Provincia(this);
        }
    }
}