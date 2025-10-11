package com.tp.dto;

public class ConserjeDTO {
    private String nombre;
    private String apellido;
    private String usuario;
    private String contrasenia;

    private ConserjeDTO(Builder builder) {
        this.nombre = builder.nombre;
        this.apellido = builder.apellido;
        this.usuario = builder.usuario;
        this.contrasenia = builder.contrasenia;
    }

    // Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public void setContrasenia(String contrasenia) { this.contrasenia = contrasenia; }

    // Getters
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getUsuario() { return usuario; }
    public String getContrasenia() { return contrasenia; }

    public static class Builder {
        private String nombre;
        private String apellido;
        private String usuario;
        private String contrasenia;

        public Builder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public Builder apellido(String apellido) {
            this.apellido = apellido;
            return this;
        }

        public Builder usuario(String usuario) {
            this.usuario = usuario;
            return this;
        }

        public Builder contrasenia(String contrasenia) {
            this.contrasenia = contrasenia;
            return this;
        }

        public ConserjeDTO build() {
            return new ConserjeDTO(this);
        }
    }
}
