package com.tp.modelo;

import java.time.LocalDate;
import java.util.List;

public class Ocupacion {

    private String id;
    private LocalDate fechaIngreso;
    private LocalDate fechaEgreso;
    private Huesped responsable;
    private List<Huesped> acompanantes;

    private Ocupacion(Builder builder) {
        this.id = builder.id;
        this.fechaIngreso = builder.fechaIngreso;
        this.fechaEgreso = builder.fechaEgreso;
        this.responsable = builder.responsable;
        this.acompanantes = builder.acompanantes;
    }

    // Getters
    public String getId() { return id; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public LocalDate getFechaEgreso() { return fechaEgreso; }
    public Huesped getResponsable() { return responsable; }
    public List<Huesped> getAcompanantes() { return acompanantes; }

    // Setters
    public void setId(String id) { this.id = id; }
    
    // Builder
    public static class Builder {
        private String id;
        private LocalDate fechaIngreso;
        private LocalDate fechaEgreso;
        private Huesped responsable;
        private List<Huesped> acompanantes;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder fechaIngreso(LocalDate fechaIngreso) {
            this.fechaIngreso = fechaIngreso;
            return this;
        }

        public Builder fechaEgreso(LocalDate fechaEgreso) {
            this.fechaEgreso = fechaEgreso;
            return this;
        }

        public Builder responsable(Huesped responsable) {
            this.responsable = responsable;
            return this;
        }

        public Builder acompanantes(List<Huesped> acompanantes) {
            this.acompanantes = acompanantes;
            return this;
        }

        public Ocupacion build() {
            return new Ocupacion(this);
        }
    }
}