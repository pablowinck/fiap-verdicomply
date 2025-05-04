package com.github.pablowinck.verdicomplyapi.repository;

import com.github.pablowinck.verdicomplyapi.model.Auditoria;
import com.github.pablowinck.verdicomplyapi.model.Conformidade;
import com.github.pablowinck.verdicomplyapi.model.NormaAmbiental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConformidadeRepository extends JpaRepository<Conformidade, Long> {

    List<Conformidade> findByAuditoria(Auditoria auditoria);
    
    List<Conformidade> findByEstaConforme(String estaConforme);
    
    List<Conformidade> findByNorma(NormaAmbiental norma);
}
