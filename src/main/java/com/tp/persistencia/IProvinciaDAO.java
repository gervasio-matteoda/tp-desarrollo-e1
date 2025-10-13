package com.tp.persistencia;
import com.tp.excepciones.*;
import com.tp.modelo.Provincia;

import java.util.List;
import java.util.function.Predicate;

public interface IProvinciaDAO {

    public List<Provincia> findBy(Predicate<Provincia> filtro) throws PersistenciaException;
    public List<Provincia> findAll() throws Exception;
}
