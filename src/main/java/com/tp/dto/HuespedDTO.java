package com.tp.dto;

import java.time.LocalDate;

public class HuespedDTO {


    private String nombre;
    private String apellido;
    private String cuit;
    private String tipoDocumento;
    private String nroDocumento;
    private LocalDate fechaDeNacimiento;
    private String email;
    private String telefono;
    private String ocupacion;
    private DireccionDTO direccion;

    private HuespedDTO(Builder builder) {
        this.nombre = builder.nombre;
        this.apellido = builder.apellido;
        this.cuit = builder.cuit;
        this.tipoDocumento = builder.tipoDocumento;
        this.nroDocumento = builder.nroDocumento;
        this.fechaDeNacimiento = builder.fechaDeNacimiento;
        this.email = builder.email;
        this.telefono = builder.telefono;
        this.ocupacion = builder.ocupacion;
        this.direccion = builder.direccion;
    }

    // Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setCuit(String cuit) { this.cuit = cuit; }
    public void setTipoDocumento(String tipoDoc) { this.tipoDocumento = tipoDoc;}
    public void setNroDocumento(String nroDocumento) { this.nroDocumento = nroDocumento; }
    public void setFechaDeNaciemiento(LocalDate fechaDeNacimiento) { this.fechaDeNacimiento = fechaDeNacimiento; }
    public void setEmail(String email) { this.email = email; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setOcupacion(String ocupacion) { this.ocupacion = ocupacion; }
    public void setDireccion(DireccionDTO direccion) { this.direccion = direccion; }

    // Getters
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getCuit() { return cuit; }
    public String getTipoDocumento() { return tipoDocumento; }
    public String getNroDocumento() { return nroDocumento; }
    public LocalDate getFechaDeNacimiento() { return fechaDeNacimiento; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getOcupacion() { return ocupacion; }
    public DireccionDTO getDireccion() { return direccion; }

    // Metodos
    public String toString(){return apellido + "," + nombre;}


    // Builder
    public static class Builder {
        private String nombre;
        private String apellido;
        private String cuit;
        private String tipoDocumento;
        private String nroDocumento;
        private LocalDate fechaDeNacimiento;
        private String email;
        private String telefono;
        private String ocupacion;
        private DireccionDTO direccion;

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
        public Builder tipoDocumento(String tipoDocumento) {
            this.tipoDocumento = tipoDocumento;
            return this;
        }
        public Builder nroDocumento(String nroDocumento) {
            this.nroDocumento = nroDocumento;
            return this;
        }
        public Builder fechaDeNacimiento(LocalDate fechaDeNacimiento) {
            this.fechaDeNacimiento = fechaDeNacimiento;
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
        public Builder direccion(DireccionDTO direccion) {
            this.direccion = direccion;
            return this;
        }
        public HuespedDTO build() {
            return new HuespedDTO(this);
        }
    }
}
