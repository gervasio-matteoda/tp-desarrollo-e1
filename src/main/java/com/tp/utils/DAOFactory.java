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

    public static IDireccionDAO getDireccionDAO() {
        return DireccionArchivoDAO.getInstancia();
    }

    public static IPaisDAO getPaisDAO() {
        return PaisArchivoDAO.getInstancia();
    }

    public static IProvinciaDAO getProvinciaDAO() {
        return ProvinciaArchivoDAO.getInstancia();
    }

    public static ICiudadDAO getCiudadDAO() {
        return CiudadArchivoDAO.getInstancia();
    }
}
