package com.github.pablowinck.verdicomplyapi.service;

import com.github.pablowinck.verdicomplyapi.dto.PendenciaDTO;

import java.time.LocalDate;
import java.util.List;

public interface PendenciaService {

    List<PendenciaDTO> listarTodas();

    PendenciaDTO buscarPorId(Long id);

    PendenciaDTO criar(PendenciaDTO pendenciaDTO);

    PendenciaDTO atualizar(Long id, PendenciaDTO pendenciaDTO);

    void excluir(Long id);

    List<PendenciaDTO> buscarPorConformidade(Long conformidadeId);

    List<PendenciaDTO> buscarPorStatus(String resolvida);

    List<PendenciaDTO> buscarPendenciasVencidas(LocalDate data);
}
