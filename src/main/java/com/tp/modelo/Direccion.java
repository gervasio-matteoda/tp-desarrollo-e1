package com.tp.modelo;

public class Direccion {

    private int codigoPostal;
    private String calle;
    private int nroCalle;
    private int nroDepartamento;
    private Ciudad ciudad;

    private Direccion(Builder builder){
        this.codigoPostal = builder.codigoPostal;
        this.calle = builder.calle;
        this.nroCalle = builder.nroCalle;
        this.nroDepartamento = builder.nroDepartamento;
        this.ciudad = builder.ciudad;
    }

    //Setters
    public void setCodigoPostal(int codigoPostal) {this.codigoPostal = codigoPostal;}
    public void setCalle(String calle) {this.calle = calle;}
    public void setNroCalle(int nroCalle) {this.nroCalle = nroCalle;}
    public void setNroDepartamento(int nroDepartamento) {this.nroDepartamento = nroDepartamento;}
    public void setCiudad(Ciudad ciudad) {this.ciudad = ciudad;}

    //Getters
    public int getCodigoPostal() {return codigoPostal;}
    public String getCalle() {return calle;}
    public int getNroCalle() {return nroCalle;}
    public int getNroDepartamento() {return nroDepartamento;}
    public Ciudad getCiudad() {return ciudad;}
    

    public static class Builder {
        private int codigoPostal;
        private String calle;
        private int nroCalle;
        private int nroDepartamento;
        private Ciudad ciudad;

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

        public Builder ciudad(Ciudad ciudad){
            this.ciudad = ciudad;
            return this;
        }

        public Direccion build(){
            return new Direccion(this);
        }
    }
}
