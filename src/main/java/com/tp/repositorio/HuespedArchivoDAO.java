package com.tp.repositorio;

import com.tp.modelo.Huesped;
import com.tp.persistencia.IHuespedDAO;

import java.util.List;
import java.util.function.Predicate;

public class HuespedArchivoDAO implements IHuespedDAO{

    @Override
    public void save(Huesped huesped) {
        // Implementación para guardar un huesped en un archivo
    }

    @Override
    public List<Huesped> findBy(Predicate<Huesped> filtro) {
        // Implementación para buscar huespedes en un archivo según un filtro
        return null;
    }

    @Override
    public List<Huesped> findAll() {
        // Implementación para obtener todos los huespedes de un archivo
        return null;
    }

    @Override
    public void delete(Huesped huesped) {
        // Implementación para eliminar un huesped de un archivo
    }
}
