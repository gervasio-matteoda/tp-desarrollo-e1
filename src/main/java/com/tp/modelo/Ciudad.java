package com.tp.modelo;

public class Ciudad {

    private int id;
    private String nombre;
    private Provincia provincia;

    private Ciudad(Builder builder) { // Constructor privado
        this.id = builder.id;
        this.nombre = builder.nombre;
        this.provincia = builder.provincia;
    }
    
    // Constructor anterior
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

    // Builder
    public static class Builder {
        private int id;
        private String nombre;
        private Provincia provincia;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public Builder provincia(Provincia provincia) {
            this.provincia = provincia;
            return this;
        }

        public Ciudad build() {
            return new Ciudad(this);
        }
    }
}