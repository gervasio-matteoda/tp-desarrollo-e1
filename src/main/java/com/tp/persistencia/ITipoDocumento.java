package com.tp.persistencia;

import java.util.List;
import java.util.function.Predicate;

import com.tp.modelo.TipoDocumento;

public interface ITipoDocumento {
    
    public List<TipoDocumento> findBy(Predicate<TipoDocumento> filtro) throws Exception;
    public List<TipoDocumento> findAll() throws Exception;
}
