package com.tp.utils;
import com.tp.persistencia.*;
import com.tp.repositorio.*;

public class DAOFactory {

    private DAOFactory() {}

    public static IConserjeDAO getConserjeDAO() {
        return ConserjeArchivoDAO.getInstancia();
    }

    public static IHuespedDAO getHuespedDAO() {
        return HuespedArchivoDAO.getInstancia();
    }
}
