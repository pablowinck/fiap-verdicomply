package com.github.pablowinck.verdicomplyapi.service.impl;

import com.github.pablowinck.verdicomplyapi.service.ConformidadeService;
import com.github.pablowinck.verdicomplyapi.service.LogConformidadeService;

import com.github.pablowinck.verdicomplyapi.dto.ConformidadeDTO;
import com.github.pablowinck.verdicomplyapi.dto.LogConformidadeDTO;
import com.github.pablowinck.verdicomplyapi.model.Conformidade;
import com.github.pablowinck.verdicomplyapi.model.LogConformidade;
import com.github.pablowinck.verdicomplyapi.repository.ConformidadeRepository;
import com.github.pablowinck.verdicomplyapi.repository.LogConformidadeRepository;
import com.github.pablowinck.verdicomplyapi.service.exception.RecursoNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogConformidadeServiceImpl implements LogConformidadeService {

    @Autowired
    private LogConformidadeRepository logConformidadeRepository;

    @Autowired
    private ConformidadeRepository conformidadeRepository;
    
    @Autowired
    private ConformidadeService conformidadeService;

    @Override
    @Transactional(readOnly = true)
    public List<LogConformidadeDTO> listarTodos() {
        return logConformidadeRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LogConformidadeDTO buscarPorId(Long id) {
        LogConformidade logConformidade = logConformidadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Log de conformidade não encontrado"));
        return converterParaDTO(logConformidade);
    }

    @Override
    @Transactional
    public LogConformidadeDTO criar(LogConformidadeDTO logConformidadeDTO) {
        LogConformidade logConformidade = converterParaEntidade(logConformidadeDTO);
        logConformidade = logConformidadeRepository.save(logConformidade);
        return converterParaDTO(logConformidade);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        if (!logConformidadeRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Log de conformidade não encontrado");
        }
        logConformidadeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LogConformidadeDTO> buscarPorConformidade(Long conformidadeId) {
        Conformidade conformidade = conformidadeRepository.findById(conformidadeId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conformidade não encontrada"));
        
        return logConformidadeRepository.findByConformidadeOrderByDataRegistroDesc(conformidade).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LogConformidadeDTO> buscarPorAcao(String acao) {
        return logConformidadeRepository.findByAcao(acao).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    private LogConformidade converterParaEntidade(LogConformidadeDTO dto) {
        Conformidade conformidade = conformidadeRepository.findById(dto.getConformidadeId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conformidade não encontrada"));
        
        return LogConformidade.builder()
                .id(dto.getId())
                .conformidade(conformidade)
                .acao(dto.getAcao())
                .dataRegistro(dto.getDataHora() != null ? dto.getDataHora().toLocalDate() : null)
                .detalhes(dto.getObservacoes())
                // A entidade não possui o campo usuario
                .build();
    }

    private LogConformidadeDTO converterParaDTO(LogConformidade log) {
        ConformidadeDTO conformidadeDTO = null;
        if (log.getConformidade() != null) {
            conformidadeDTO = conformidadeService.buscarPorId(log.getConformidade().getId());
        }
        
        return LogConformidadeDTO.builder()
                .id(log.getId())
                .conformidadeId(log.getConformidade() != null ? log.getConformidade().getId() : null)
                .acao(log.getAcao())
                .dataHora(log.getDataRegistro() != null ? log.getDataRegistro().atStartOfDay() : null)
                .usuario("Sistema") // Campo não existe na entidade, usando valor padrão
                .observacoes(log.getDetalhes())
                .conformidade(conformidadeDTO)
                .build();
    }
}
