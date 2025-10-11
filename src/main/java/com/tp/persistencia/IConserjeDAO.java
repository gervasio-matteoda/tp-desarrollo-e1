package com.tp.persistencia;
import com.tp.excepciones.*;
import com.tp.modelo.Conserje;

import java.util.List;
import java.util.function.Predicate;

public interface IConserjeDAO {

    public void create(Conserje conserje) throws EntidadDuplicadaException, PersistenciaException;
    public List<Conserje> findBy(Predicate<Conserje> filtro) throws PersistenciaException; 
	public List<Conserje> findAll() throws PersistenciaException;
    public void delete(List<Conserje> conserje) throws EntidadNoEncontradaException, PersistenciaException;
}