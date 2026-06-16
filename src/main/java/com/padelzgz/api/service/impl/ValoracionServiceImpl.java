package com.padelzgz.api.service.impl;

import com.padelzgz.api.exception.*;
import com.padelzgz.api.model.*;
import com.padelzgz.api.repository.*;
import com.padelzgz.api.service.ValoracionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ValoracionServiceImpl implements ValoracionService {

    @Autowired private ValoracionRepository valoracionRepository;
    @Autowired private PistaRepository pistaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ModelMapper modelMapper;

    @Override
    public Set<Valoracion> findAll() { return valoracionRepository.findAll().stream().collect(Collectors.toSet()); }

    @Override
    public Set<Valoracion> findByPistaId(long pistaId) { return valoracionRepository.findByPistaId(pistaId); }

    @Override
    public Set<Valoracion> findByUsuarioId(long usuarioId) { return valoracionRepository.findByUsuarioId(usuarioId); }

    @Override
    public Set<Valoracion> findByPuntuacion(int puntuacion) { return valoracionRepository.findByPuntuacion(puntuacion); }

    @Override
    public Set<Valoracion> findByPistaIdAndPuntuacion(long pistaId, int puntuacion) {
        return valoracionRepository.findByPistaIdAndPuntuacion(pistaId, puntuacion);
    }

    @Override
    public Optional<Valoracion> findById(long id) { return valoracionRepository.findById(id); }

    @Override
    public Valoracion addValoracion(Valoracion valoracion, long pistaId, long usuarioId) {
        Pista pista = pistaRepository.findById(pistaId).orElseThrow(() -> new PistaNotFoundException(pistaId));
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new UsuarioNotFoundException(usuarioId));
        valoracion.setPista(pista);
        valoracion.setUsuario(usuario);
        valoracion.setFechaValoracion(LocalDate.now());
        return valoracionRepository.save(valoracion);
    }

    @Override
    public Valoracion modifyValoracion(long id, Valoracion newValoracion) {
        Valoracion existing = valoracionRepository.findById(id).orElseThrow(() -> new ValoracionNotFoundException(id));
        existing.setPuntuacion(newValoracion.getPuntuacion());
        existing.setComentario(newValoracion.getComentario());
        existing.setFechaValoracion(newValoracion.getFechaValoracion());
        existing.setVerificada(newValoracion.isVerificada());
        existing.setVisibilidad(newValoracion.getVisibilidad());
        existing.setUtilCount(newValoracion.getUtilCount());
        return valoracionRepository.save(existing);
    }

    @Override
    public Valoracion patchValoracion(long id, Map<String, Object> fields) {
        Valoracion existing = valoracionRepository.findById(id).orElseThrow(() -> new ValoracionNotFoundException(id));
        if (fields.containsKey("puntuacion"))  existing.setPuntuacion(((Number) fields.get("puntuacion")).intValue());
        if (fields.containsKey("comentario"))  existing.setComentario((String) fields.get("comentario"));
        if (fields.containsKey("visibilidad")) existing.setVisibilidad((String) fields.get("visibilidad"));
        if (fields.containsKey("verificada"))  existing.setVerificada((Boolean) fields.get("verificada"));
        return valoracionRepository.save(existing);
    }

    @Override
    public void deleteValoracion(long id) {
        valoracionRepository.findById(id).orElseThrow(() -> new ValoracionNotFoundException(id));
        valoracionRepository.deleteById(id);
    }
}
