package com.tp.servicios;

import com.tp.dto.HuespedDTO;
import com.tp.excepciones.PersistenciaException;
import com.tp.persistencia.IOcupacionDAO;
import com.tp.utils.DAOFactory;

public class OcupacionService {

    private static OcupacionService instancia;
    private final IOcupacionDAO ocupacionDAO;

    private OcupacionService() {
        this.ocupacionDAO = DAOFactory.getOcupacionDAO();
    }

    public static OcupacionService getInstancia() {
        if (instancia == null) {
            instancia = new OcupacionService();
        }
        return instancia;
    }

    public boolean huespedTieneOcupaciones(HuespedDTO huespedDTO) throws PersistenciaException {
        // Busca si el huésped aparece como responsable en alguna ocupación.
        return !ocupacionDAO.findBy(o -> 
            o.getResponsable() != null &&
            o.getResponsable().getTipoDocumento().getTipo().name().equals(huespedDTO.getTipoDocumento()) &&
            o.getResponsable().getNroDocumento().equals(huespedDTO.getNroDocumento())
        ).isEmpty();
    }
}