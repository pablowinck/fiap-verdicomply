package com.github.pablowinck.verdicomplyapi.repository;

import com.github.pablowinck.verdicomplyapi.model.Conformidade;
import com.github.pablowinck.verdicomplyapi.model.LogConformidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogConformidadeRepository extends JpaRepository<LogConformidade, Long> {

    List<LogConformidade> findByConformidadeOrderByDataRegistroDesc(Conformidade conformidade);
    
    List<LogConformidade> findByAcao(String acao);
}
