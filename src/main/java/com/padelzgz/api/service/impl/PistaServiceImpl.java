package com.padelzgz.api.service.impl;

import com.padelzgz.api.exception.*;
import com.padelzgz.api.model.*;
import com.padelzgz.api.repository.*;
import com.padelzgz.api.service.PistaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PistaServiceImpl implements PistaService {

    @Autowired private PistaRepository pistaRepository;
    @Autowired private ClubRepository clubRepository;
    @Autowired private ModelMapper modelMapper;

    @Override
    public Set<Pista> findAll() { return pistaRepository.findAll().stream().collect(Collectors.toSet()); }

    @Override
    public Set<Pista> findByTipo(String tipo) { return pistaRepository.findByTipo(tipo); }

    @Override
    public Set<Pista> findByInterior(boolean interior) { return pistaRepository.findByInterior(interior); }

    @Override
    public Set<Pista> findByActiva(boolean activa) { return pistaRepository.findByActiva(activa); }

    @Override
    public Set<Pista> findByTipoAndInteriorAndActiva(String tipo, boolean interior, boolean activa) {
        return pistaRepository.findByTipoAndInteriorAndActiva(tipo, interior, activa);
    }

    @Override
    public Set<Pista> findByClubId(long clubId) { return pistaRepository.findByClubId(clubId); }

    @Override
    public Optional<Pista> findById(long id) { return pistaRepository.findById(id); }

    @Override
    public Pista addPista(Pista pista, long clubId) {
        Club club = clubRepository.findById(clubId).orElseThrow(() -> new ClubNotFoundException(clubId));
        pista.setClub(club);
        return pistaRepository.save(pista);
    }

    @Override
    public Pista modifyPista(long id, Pista newPista) {
        Pista existing = pistaRepository.findById(id).orElseThrow(() -> new PistaNotFoundException(id));
        Club club = existing.getClub();
        modelMapper.map(newPista, existing);
        existing.setId(id);
        existing.setClub(club);
        return pistaRepository.save(existing);
    }

    @Override
    public Pista patchPista(long id, Map<String, Object> fields) {
        Pista existing = pistaRepository.findById(id).orElseThrow(() -> new PistaNotFoundException(id));
        if (fields.containsKey("tipo"))       existing.setTipo((String) fields.get("tipo"));
        if (fields.containsKey("superficie")) existing.setSuperficie((String) fields.get("superficie"));
        if (fields.containsKey("numero"))     existing.setNumero(((Number) fields.get("numero")).intValue());
        if (fields.containsKey("precioHora")) existing.setPrecioHora(((Number) fields.get("precioHora")).floatValue());
        if (fields.containsKey("interior"))   existing.setInterior((Boolean) fields.get("interior"));
        if (fields.containsKey("activa"))     existing.setActiva((Boolean) fields.get("activa"));
        return pistaRepository.save(existing);
    }

    @Override
    public void deletePista(long id) {
        pistaRepository.findById(id).orElseThrow(() -> new PistaNotFoundException(id));
        pistaRepository.deleteById(id);
    }

    @Override
    public Set<Pista> findByPrecioHoraBetween(float min, float max) {
        return pistaRepository.findByPrecioHoraBetween(min, max);
    }

    @Override
    public Set<Pista> findByPuntuacionMediaMinima(float minPuntuacion) {
        Set<Long> ids = pistaRepository.findIdsByPuntuacionMediaMinima(minPuntuacion);
        return ids.stream()
                .map(i -> pistaRepository.findById(i).orElse(null))
                .filter(p -> p != null)
                .collect(Collectors.toSet());
    }
}
