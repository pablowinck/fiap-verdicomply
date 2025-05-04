package com.github.pablowinck.verdicomplyapi.service;

import com.github.pablowinck.verdicomplyapi.dto.AuditoriaDTO;

import java.util.List;

public interface AuditoriaService {
    
    List<AuditoriaDTO> listarTodas();
    
    AuditoriaDTO buscarPorId(Long id);
    
    AuditoriaDTO criar(AuditoriaDTO auditoriaDTO);
    
    AuditoriaDTO atualizar(Long id, AuditoriaDTO auditoriaDTO);
    
    void excluir(Long id);
    
    List<AuditoriaDTO> buscarPorStatus(String status);
    
    List<AuditoriaDTO> buscarPorDepartamento(Long departamentoId);
}
