package com.tp.persistencia;
import com.tp.modelo.Huesped;
import com.tp.excepciones.*;

import java.util.List;
import java.util.function.Predicate;

public interface IHuespedDAO {

    public void create(Huesped huesped) throws EntidadDuplicadaException, PersistenciaException;
    public void update(Huesped huesped) throws EntidadNoEncontradaException, PersistenciaException;
    public List<Huesped> findBy(Predicate<Huesped> filtro) throws PersistenciaException; 
	public List<Huesped> findAll() throws PersistenciaException;
    public void delete(Huesped huesped) throws EntidadNoEncontradaException, PersistenciaException;

}
