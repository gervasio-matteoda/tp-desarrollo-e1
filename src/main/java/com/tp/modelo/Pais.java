package com.tp.modelo;

public class Pais {
    
    private int id;
    private String nombre;

    private Pais(Builder builder) {
        this.id = builder.id;
        this.nombre = builder.nombre;
    }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }

    // Builder
    public static class Builder {
        private int id;
        private String nombre;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public Pais build() {
            return new Pais(this);
        }
    }
}