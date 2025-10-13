package com.tp.modelo;

public class TipoDocumento {

    public enum tipoDocumentoEnum {DNI, PASAPORTE,LE, LC, OTRO}

    private tipoDocumentoEnum tipo;
    private String id;

    public TipoDocumento(tipoDocumentoEnum tipo, String id) {
        this.tipo = tipo;
        this.id = id;
    }

    public TipoDocumento() {}

    // Setters
    public void setTipo(tipoDocumentoEnum tipo) { this.tipo = tipo; }
    public void setId(String id) { this.id = id; }

    // Getters
    public tipoDocumentoEnum getTipo() { return tipo; }
    public String getId() { return id; } 

    //Metodos
    @Override
    public String toString(){return tipo.toString();}
    public tipoDocumentoEnum valueOf(String tipo){ return tipoDocumentoEnum.valueOf(tipo); }
}
