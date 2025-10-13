package com.tp.persistencia;
import com.tp.excepciones.*;
import com.tp.modelo.Pais;

import java.util.List;
import java.util.function.Predicate;

public interface IPaisDAO {

    public List<Pais> findBy(Predicate<Pais> filtro) throws PersistenciaException;
    public List<Pais> findAll() throws Exception;
}
