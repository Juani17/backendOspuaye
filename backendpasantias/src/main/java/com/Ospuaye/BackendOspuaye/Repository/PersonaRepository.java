package com.Ospuaye.BackendOspuaye.Repository;

import com.Ospuaye.BackendOspuaye.Entity.Enum.TipoDocumento;
import com.Ospuaye.BackendOspuaye.Entity.Persona;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaRepository extends BaseRepository<Persona, Long> {
    Optional<Persona> findByDni(Long dni);
    Optional<Persona> findByCuil(Long cuil);
    Optional<Persona> findByTipoDocumentoAndDni(TipoDocumento tipoDocumento, Long dni);
}
