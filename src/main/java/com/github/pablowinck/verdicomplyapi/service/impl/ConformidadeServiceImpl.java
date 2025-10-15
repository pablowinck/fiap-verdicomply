package com.github.pablowinck.verdicomplyapi.service.impl;

import com.github.pablowinck.verdicomplyapi.dto.AuditoriaDTO;
import com.github.pablowinck.verdicomplyapi.dto.ConformidadeDTO;
import com.github.pablowinck.verdicomplyapi.service.ConformidadeService;
import com.github.pablowinck.verdicomplyapi.dto.NormaAmbientalDTO;
import com.github.pablowinck.verdicomplyapi.model.Auditoria;
import com.github.pablowinck.verdicomplyapi.model.Conformidade;
import com.github.pablowinck.verdicomplyapi.model.NormaAmbiental;
import com.github.pablowinck.verdicomplyapi.repository.AuditoriaRepository;
import com.github.pablowinck.verdicomplyapi.repository.ConformidadeRepository;
import com.github.pablowinck.verdicomplyapi.repository.NormaAmbientalRepository;
import com.github.pablowinck.verdicomplyapi.service.exception.RecursoNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConformidadeServiceImpl implements ConformidadeService {

    @Autowired
    private ConformidadeRepository conformidadeRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private NormaAmbientalRepository normaAmbientalRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ConformidadeDTO> listarTodas() {
        return conformidadeRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ConformidadeDTO buscarPorId(Long id) {
        Conformidade conformidade = conformidadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conformidade não encontrada"));
        return converterParaDTO(conformidade);
    }

    @Override
    @Transactional
    public ConformidadeDTO criar(ConformidadeDTO conformidadeDTO) {
        Conformidade conformidade = converterParaEntidade(conformidadeDTO);
        conformidade = conformidadeRepository.save(conformidade);
        return converterParaDTO(conformidade);
    }

    @Override
    @Transactional
    public ConformidadeDTO atualizar(Long id, ConformidadeDTO conformidadeDTO) {
        if (!conformidadeRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Conformidade não encontrada");
        }
        
        Conformidade conformidade = converterParaEntidade(conformidadeDTO);
        conformidade.setId(id);
        conformidade = conformidadeRepository.save(conformidade);
        return converterParaDTO(conformidade);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        if (!conformidadeRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Conformidade não encontrada");
        }
        conformidadeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConformidadeDTO> buscarPorAuditoria(Long auditoriaId) {
        Auditoria auditoria = auditoriaRepository.findById(auditoriaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Auditoria não encontrada"));
        
        return conformidadeRepository.findByAuditoria(auditoria).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConformidadeDTO> buscarPorStatus(String estaConforme) {
        return conformidadeRepository.findByEstaConforme(estaConforme).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConformidadeDTO> buscarPorNorma(Long normaAmbientalId) {
        NormaAmbiental norma = normaAmbientalRepository.findById(normaAmbientalId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Norma ambiental não encontrada"));
        
        return conformidadeRepository.findByNorma(norma).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    private Conformidade converterParaEntidade(ConformidadeDTO dto) {
        Auditoria auditoria = auditoriaRepository.findById(dto.getAuditoriaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Auditoria não encontrada"));
        
        NormaAmbiental normaAmbiental = normaAmbientalRepository.findById(dto.getNormaAmbientalId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Norma ambiental não encontrada"));
        
        return Conformidade.builder()
                .id(dto.getId())
                .auditoria(auditoria)
                .norma(normaAmbiental)
                .estaConforme(dto.getEstaConforme())
                .observacao(dto.getObservacao())
                .build();
    }

    private ConformidadeDTO converterParaDTO(Conformidade conformidade) {
        AuditoriaDTO auditoriaDTO = null;
        if (conformidade.getAuditoria() != null) {
            Auditoria auditoria = conformidade.getAuditoria();
            auditoriaDTO = AuditoriaDTO.builder()
                    .id(auditoria.getId())
                    .departamentoId(auditoria.getDepartamento() != null ? auditoria.getDepartamento().getId() : null)
                    .dataAuditoria(auditoria.getDataAuditoria())
                    .auditorResponsavel(auditoria.getAuditorResponsavel())
                    .statusAuditoria(auditoria.getStatusAuditoria())
                    .build();
        }
        
        NormaAmbientalDTO normaDTO = null;
        if (conformidade.getNorma() != null) {
            NormaAmbiental norma = conformidade.getNorma();
            normaDTO = NormaAmbientalDTO.builder()
                    .id(norma.getId())
                    .codigoNorma(norma.getCodigoNorma())
                    .titulo(norma.getTitulo())
                    .descricao(norma.getDescricao())
                    .orgaoFiscalizador(norma.getOrgaoFiscalizador())
                    .severidade(norma.getSeveridade())
                    .build();
        }
        
        return ConformidadeDTO.builder()
                .id(conformidade.getId())
                .auditoriaId(conformidade.getAuditoria() != null ? conformidade.getAuditoria().getId() : null)
                .normaAmbientalId(conformidade.getNorma() != null ? conformidade.getNorma().getId() : null)
                .estaConforme(conformidade.getEstaConforme())
                .observacao(conformidade.getObservacao())
                .auditoria(auditoriaDTO)
                .normaAmbiental(normaDTO)
                .build();
    }
}
