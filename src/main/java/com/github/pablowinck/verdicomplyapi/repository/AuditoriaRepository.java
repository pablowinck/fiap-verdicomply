package com.github.pablowinck.verdicomplyapi.repository;

import com.github.pablowinck.verdicomplyapi.model.Auditoria;
import com.github.pablowinck.verdicomplyapi.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByStatusAuditoria(String statusAuditoria);
    
    List<Auditoria> findByStatusAuditoriaContainingIgnoreCase(String statusAuditoria);
    
    List<Auditoria> findByDepartamento(Departamento departamento);
}
