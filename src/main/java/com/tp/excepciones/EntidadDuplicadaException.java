package com.tp.excepciones;

public class EntidadDuplicadaException extends PersistenciaException {

    public EntidadDuplicadaException(String message) {
        super(message);
    }
}