package com.tp.servicios;

import com.tp.excepciones.*;
import com.tp.modelo.Huesped;
import com.tp.persistencia.IHuespedDAO;
import com.tp.utils.DAOFactory;

public class HuespedService {
    private static HuespedService instancia;
    private IHuespedDAO huespedDAO;

    private HuespedService() {
        this.huespedDAO = DAOFactory.getHuespedDAO();
    }
    
    public static HuespedService getInstancia() {
        if (instancia == null) {
            instancia = new HuespedService();
        }
        return instancia;
    }

    public void registrarHuesped(Huesped huesped) {
        try {
            huespedDAO.create(huesped);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    private void validarHuesped(Huesped huesped) throws ValidacionException {
        if (huesped.getNombre() == null || huesped.getNombre().isBlank() || huesped.getNombre().length() > 64) {
            throw new ValidacionException("El nombre del huesped es inválido. El campo se encuentra vacío o excede 64 caracteres.");
        }
        if (huesped.getApellido() == null || huesped.getApellido().isBlank() || huesped.getApellido().length() > 64) {
            throw new ValidacionException("El apellido del huesped es inválido. El campo se encuentra vacío o excede 64 caracteres.");
        }
        if (huesped.getCuit() != null && (huesped.getCuit().isBlank() || huesped.getCuit().length() > 11)) {
            throw new ValidacionException("El CUIT del huesped es inválido. El campo se encuentra vacío o excede 11 caracteres.");
        }
        if (huesped.getTipoDocumento() == null) {
            throw new ValidacionException("El tipo de documento del huesped es inválido. El campo se encuentra vacío");
        }
        if ( huesped.getNroDocumento() == null || huesped.getNroDocumento().isBlank() || huesped.getNroDocumento().length() > 64) {
            throw new ValidacionException("El numero de documento del huesped es inválido. El campo se encuentra vacío o excede 64 caracteres.");
        }
        
    }
}
