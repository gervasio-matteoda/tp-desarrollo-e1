package com.tp.excepciones;

public class EntidadNoEncontradaException extends PersistenciaException {

    public EntidadNoEncontradaException(String message) {
        super(message);
    }
}