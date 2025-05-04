package com.github.pablowinck.verdicomplyapi.repository;

import com.github.pablowinck.verdicomplyapi.model.Conformidade;
import com.github.pablowinck.verdicomplyapi.model.Pendencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PendenciaRepository extends JpaRepository<Pendencia, Long> {

    List<Pendencia> findByConformidade(Conformidade conformidade);
    
    List<Pendencia> findByResolvida(String resolvida);
    
    List<Pendencia> findByResolvidaAndPrazoResolucaoBefore(String resolvida, LocalDate data);
}
