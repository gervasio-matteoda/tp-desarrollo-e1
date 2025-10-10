package com.tp.utils;
import com.tp.excepciones.*;
import com.tp.persistencia.*;
import com.tp.repositorio.*;

public class DAOFactory {

    private static IConserjeDAO conserjeDAO = null;
    private static IHuespedDAO huespedDAO = null;

    private DAOFactory() {}

    public static IConserjeDAO getConserjeDAO() throws PersistenciaException {
        if (conserjeDAO == null)
            conserjeDAO = new ConserjeArchivoDAO();
        return conserjeDAO;
    }

    public static IHuespedDAO getHuespedDAO() throws PersistenciaException {
        if (huespedDAO == null)
            huespedDAO = new HuespedArchivoDAO();
        return huespedDAO;
    }
}
