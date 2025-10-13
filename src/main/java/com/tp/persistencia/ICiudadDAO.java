package com.tp.persistencia;
import com.tp.excepciones.*;
import com.tp.modelo.Ciudad;

import java.util.List;
import java.util.function.Predicate;

public interface ICiudadDAO {

    public List<Ciudad> findBy(Predicate<Ciudad> filtro) throws PersistenciaException;
    public List<Ciudad> findAll() throws Exception;
}
