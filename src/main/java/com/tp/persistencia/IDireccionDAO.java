package com.tp.persistencia;
import com.tp.modelo.Direccion;
import com.tp.excepciones.*;

import java.util.List;
import java.util.function.Predicate;


public interface IDireccionDAO {

    public Direccion create(Direccion direccion) throws PersistenciaException;
    public void update(Direccion direccion) throws EntidadNoEncontradaException, PersistenciaException;
    public List<Direccion> findBy(Predicate<Direccion> filtro) throws PersistenciaException;
    public List<Direccion> findAll() throws PersistenciaException; 
    public void delete(Direccion direccion) throws EntidadNoEncontradaException, PersistenciaException;
}