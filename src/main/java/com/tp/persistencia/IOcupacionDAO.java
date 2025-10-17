package com.tp.persistencia;

import com.tp.excepciones.PersistenciaException;
import com.tp.modelo.Ocupacion;
import java.util.List;
import java.util.function.Predicate;

public interface IOcupacionDAO {
    List<Ocupacion> findBy(Predicate<Ocupacion> filtro) throws PersistenciaException;
    List<Ocupacion> findAll() throws PersistenciaException;
}