package com.tp.modelo;

public class TipoDocumento {

    public enum tipoDocumentoEnum {DNI, PASAPORTE, CUIT, CUIL, OTRO}

    private tipoDocumentoEnum tipo;
    private String id;

    public TipoDocumento(tipoDocumentoEnum tipo, String id) {
        this.tipo = tipo;
        this.id = id;
    }

    // Setters
    public void setTipo(tipoDocumentoEnum tipo) { this.tipo = tipo; }
    public void setId(String id) { this.id = id; }

    // Getters
    public tipoDocumentoEnum getTipo() { return tipo; }
    public String getId() { return id; } 
}
