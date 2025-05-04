package com.github.pablowinck.verdicomplyapi.repository;

import com.github.pablowinck.verdicomplyapi.model.NormaAmbiental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NormaAmbientalRepository extends JpaRepository<NormaAmbiental, Long> {
    
    Optional<NormaAmbiental> findByCodigoNorma(String codigoNorma);
    
    List<NormaAmbiental> findByOrgaoFiscalizador(String orgaoFiscalizador);
}
