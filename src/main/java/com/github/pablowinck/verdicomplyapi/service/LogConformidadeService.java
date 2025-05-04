package com.github.pablowinck.verdicomplyapi.service;

import com.github.pablowinck.verdicomplyapi.dto.LogConformidadeDTO;

import java.util.List;

public interface LogConformidadeService {

    List<LogConformidadeDTO> listarTodos();

    LogConformidadeDTO buscarPorId(Long id);

    LogConformidadeDTO criar(LogConformidadeDTO logConformidadeDTO);

    void excluir(Long id);

    List<LogConformidadeDTO> buscarPorConformidade(Long conformidadeId);

    List<LogConformidadeDTO> buscarPorAcao(String acao);
}
