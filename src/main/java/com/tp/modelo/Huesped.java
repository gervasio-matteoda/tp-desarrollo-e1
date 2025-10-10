package com.tp.modelo;

import java.util.Date;

public class Huesped {

    private String nombre;
    private String apellido;
    private String cuit;
    private TipoDocumento tipoDocumento;
    private String nroDocumento;
    private Date fechaDeNaciemiento;
    private String email;
    private String telefono;
    private String ocupacion;
    private Direccion direccion;

    private Huesped(Builder builder) {
        this.nombre = builder.nombre;
        this.apellido = builder.apellido;
        this.cuit = builder.cuit;
        this.tipoDocumento = builder.tipoDocumento;
        this.nroDocumento = builder.nroDocumento;
        this.fechaDeNaciemiento = builder.fechaDeNaciemiento;
        this.email = builder.email;
        this.telefono = builder.telefono;
        this.ocupacion = builder.ocupacion;
        this.direccion = builder.direccion;
    }

    // Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setCuit(String cuit) { this.cuit = cuit; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public void setNroDocumento(String nroDocumento) { this.nroDocumento = nroDocumento; }
    public void setFechaDeNaciemiento(Date fechaDeNaciemiento) { this.fechaDeNaciemiento = fechaDeNaciemiento; }
    public void setEmail(String email) { this.email = email; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setOcupacion(String ocupacion) { this.ocupacion = ocupacion; }
    public void setDireccion(Direccion direccion) { this.direccion = direccion; }

    // Getters
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getCuit() { return cuit; }
    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public String getNroDocumento() { return nroDocumento; }
    public Date getFechaDeNaciemiento() { return fechaDeNaciemiento; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getOcupacion() { return ocupacion; }
    public Direccion getDireccion() { return direccion; }

    // Metodos
    public String toString(){return apellido + "," + nombre;}


    // Builder
    public static class Builder {
        private String nombre;
        private String apellido;
        private String cuit;
        private TipoDocumento tipoDocumento;
        private String nroDocumento;
        private Date fechaDeNaciemiento;
        private String email;
        private String telefono;
        private String ocupacion;
        private Direccion direccion;

        public Builder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }
        public Builder apellido(String apellido) {
            this.apellido = apellido;
            return this;
        }
        public Builder cuit(String cuit) {
            this.cuit = cuit;
            return this;
        }
        public Builder tipoDocumento(TipoDocumento tipoDocumento) {
            this.tipoDocumento = tipoDocumento;
            return this;
        }
        public Builder nroDocumento(String nroDocumento) {
            this.nroDocumento = nroDocumento;
            return this;
        }
        public Builder fechaDeNaciemiento(Date fechaDeNaciemiento) {
            this.fechaDeNaciemiento = fechaDeNaciemiento;
            return this;
        }
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        public Builder telefono(String telefono) {
            this.telefono = telefono;
            return this;
        }
        public Builder ocupacion(String ocupacion) {
            this.ocupacion = ocupacion;
            return this;
        }
        public Builder direccion(Direccion direccion) {
            this.direccion = direccion;
            return this;
        }
        public Huesped build() {
            return new Huesped(this);
        }
    }
}
