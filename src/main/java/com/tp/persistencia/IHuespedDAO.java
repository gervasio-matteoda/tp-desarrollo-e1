package com.tp.persistencia;
import com.tp.modelo.Huesped;

import java.util.List;
import java.util.function.Predicate;

public interface IHuespedDAO {

    public void save(Huesped huesped);
    public List<Huesped> findBy(Predicate<Huesped> filtro); 
	public List<Huesped> findAll();
    public void delete(Huesped huesped);

}
