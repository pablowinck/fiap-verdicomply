package com.github.pablowinck.verdicomplyapi.service;

import com.github.pablowinck.verdicomplyapi.dto.ConformidadeDTO;

import java.util.List;

public interface ConformidadeService {

    List<ConformidadeDTO> listarTodas();

    ConformidadeDTO buscarPorId(Long id);

    ConformidadeDTO criar(ConformidadeDTO conformidadeDTO);

    ConformidadeDTO atualizar(Long id, ConformidadeDTO conformidadeDTO);

    void excluir(Long id);

    List<ConformidadeDTO> buscarPorAuditoria(Long auditoriaId);

    List<ConformidadeDTO> buscarPorStatus(String estaConforme);

    List<ConformidadeDTO> buscarPorNorma(Long normaAmbientalId);
}
