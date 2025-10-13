package com.tp.dto;

public class DireccionDTO {
    private String id;
    private int codigoPostal;
    private String calle;
    private int nroCalle;
    private int nroDepartamento;
    private int nroPiso;
    private String ciudad;
    private String provincia;
    private String pais;

    private DireccionDTO(Builder builder){
        this.id = builder.id;
        this.codigoPostal = builder.codigoPostal;
        this.calle = builder.calle;
        this.nroCalle = builder.nroCalle;
        this.nroDepartamento = builder.nroDepartamento;
        this.nroPiso = builder.nroPiso;
        this.ciudad = builder.ciudad;
        this.provincia = builder.provincia;
        this.pais = builder.pais;
    }

    //Setters
    public void setId(String id) {this.id = id;}
    public void setCodigoPostal(int codigoPostal) {this.codigoPostal = codigoPostal;}
    public void setCalle(String calle) {this.calle = calle;}
    public void setNroCalle(int nroCalle) {this.nroCalle = nroCalle;}
    public void setNroDepartamento(int nroDepartamento) {this.nroDepartamento = nroDepartamento;}
    public void setNroPiso(int nroPiso) {this.nroPiso = nroPiso;}
    public void setCiudad(String ciudad) {this.ciudad = ciudad;}
    public void setProvincia(String provincia) {this.provincia = provincia;}
    public void setPais(String pais) {this.pais = pais;}

    //Getters
    public String getId() {return id;}
    public int getCodigoPostal() {return codigoPostal;}
    public String getCalle() {return calle;}
    public int getNroCalle() {return nroCalle;}
    public int getNroDepartamento() {return nroDepartamento;}
    public int getNroPiso() {return nroPiso;}
    public String getCiudad() {return ciudad;}
    public String getProvincia() {return provincia;}
    public String getPais() {return pais;}
    

    public static class Builder {
        private String id;
        private int codigoPostal;
        private String calle;
        private int nroCalle;
        private int nroDepartamento;
        private int nroPiso;
        private String ciudad;
        private String provincia;
        private String pais;

        public Builder id(String id){
            this.id = id;
            return this;
        }
        public Builder codigoPostal(int codigoPostal){
            this.codigoPostal = codigoPostal;
            return this;
        }

        public Builder calle(String calle){
            this.calle = calle;
            return this;
        }

        public Builder nroCalle(int nroCalle){
            this.nroCalle = nroCalle;
            return this;
        }

        public Builder nroDepartamento(int nroDepartamento){
            this.nroDepartamento = nroDepartamento;
            return this;
        }

        public Builder nroPiso(int nroPiso){
            this.nroPiso = nroPiso;
            return this;
        }

        public Builder ciudad(String ciudad){
            this.ciudad = ciudad;
            return this;
        }

        public Builder provincia(String provincia){
            this.provincia = provincia;
            return this;
        }

        public Builder pais(String pais){
            this.pais = pais;
            return this;
        }

        public DireccionDTO build(){
            return new DireccionDTO(this);
        }
    }
}
