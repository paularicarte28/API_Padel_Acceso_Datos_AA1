package com.padelzgz.api.service;

import com.padelzgz.api.model.Valoracion;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ValoracionService {
    Set<Valoracion> findAll();
    Set<Valoracion> findByPistaId(long pistaId);
    Set<Valoracion> findByUsuarioId(long usuarioId);
    Set<Valoracion> findByPuntuacion(int puntuacion);
    Set<Valoracion> findByPistaIdAndPuntuacion(long pistaId, int puntuacion);
    Optional<Valoracion> findById(long id);
    Valoracion addValoracion(Valoracion valoracion, long pistaId, long usuarioId);
    Valoracion modifyValoracion(long id, Valoracion valoracion);
    Valoracion patchValoracion(long id, Map<String, Object> fields);
    void deleteValoracion(long id);
}
