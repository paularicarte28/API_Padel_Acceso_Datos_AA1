package com.padelzgz.api.service.impl;

import com.padelzgz.api.exception.ClubNotFoundException;
import com.padelzgz.api.model.Club;
import com.padelzgz.api.repository.ClubRepository;
import com.padelzgz.api.service.ClubService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClubServiceImpl implements ClubService {

    @Autowired private ClubRepository clubRepository;
    @Autowired private ModelMapper modelMapper;

    @Override
    public Set<Club> findAll() { return clubRepository.findAll().stream().collect(Collectors.toSet()); }

    @Override
    public Set<Club> findByCiudad(String ciudad) { return clubRepository.findByCiudad(ciudad); }

    @Override
    public Set<Club> findByActivo(boolean activo) { return clubRepository.findByActivo(activo); }

    @Override
    public Set<Club> findByCiudadAndActivo(String ciudad, boolean activo) {
        return clubRepository.findByCiudadAndActivo(ciudad, activo);
    }

    @Override
    public Optional<Club> findById(long id) { return clubRepository.findById(id); }

    @Override
    public Club addClub(Club club) { return clubRepository.save(club); }

    @Override
    public Club modifyClub(long id, Club newClub) {
        Club existing = clubRepository.findById(id).orElseThrow(() -> new ClubNotFoundException(id));
        modelMapper.map(newClub, existing);
        existing.setId(id);
        return clubRepository.save(existing);
    }

    @Override
    public Club patchClub(long id, Map<String, Object> fields) {
        Club existing = clubRepository.findById(id).orElseThrow(() -> new ClubNotFoundException(id));
        if (fields.containsKey("nombre"))       existing.setNombre((String) fields.get("nombre"));
        if (fields.containsKey("ciudad"))       existing.setCiudad((String) fields.get("ciudad"));
        if (fields.containsKey("direccion"))    existing.setDireccion((String) fields.get("direccion"));
        if (fields.containsKey("telefono"))     existing.setTelefono((String) fields.get("telefono"));
        if (fields.containsKey("email"))        existing.setEmail((String) fields.get("email"));
        if (fields.containsKey("activo"))       existing.setActivo((Boolean) fields.get("activo"));
        if (fields.containsKey("fechaApertura"))
            existing.setFechaApertura(LocalDate.parse(fields.get("fechaApertura").toString()));
        return clubRepository.save(existing);
    }

    @Override
    public void deleteClub(long id) {
        clubRepository.findById(id).orElseThrow(() -> new ClubNotFoundException(id));
        clubRepository.deleteById(id);
    }

    @Override
    public Set<Club> findClubesConMasPistasActivas() {
        return clubRepository.findClubesConMasPistasActivas();
    }
}
