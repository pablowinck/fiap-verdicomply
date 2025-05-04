package com.github.pablowinck.verdicomplyapi.service.impl;


import com.github.pablowinck.verdicomplyapi.service.PendenciaService;
import com.github.pablowinck.verdicomplyapi.service.ConformidadeService;

import com.github.pablowinck.verdicomplyapi.dto.ConformidadeDTO;
import com.github.pablowinck.verdicomplyapi.dto.PendenciaDTO;
import com.github.pablowinck.verdicomplyapi.model.Conformidade;
import com.github.pablowinck.verdicomplyapi.model.Pendencia;
import com.github.pablowinck.verdicomplyapi.repository.ConformidadeRepository;
import com.github.pablowinck.verdicomplyapi.repository.PendenciaRepository;
import com.github.pablowinck.verdicomplyapi.service.exception.RecursoNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PendenciaServiceImpl implements PendenciaService {

    @Autowired
    private PendenciaRepository pendenciaRepository;

    @Autowired
    private ConformidadeRepository conformidadeRepository;

    @Autowired
    private ConformidadeService conformidadeService;

    @Override
    @Transactional(readOnly = true)
    public List<PendenciaDTO> listarTodas() {
        return pendenciaRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PendenciaDTO buscarPorId(Long id) {
        Pendencia pendencia = pendenciaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pendência não encontrada"));
        return converterParaDTO(pendencia);
    }

    @Override
    @Transactional
    public PendenciaDTO criar(PendenciaDTO pendenciaDTO) {
        Pendencia pendencia = converterParaEntidade(pendenciaDTO);
        pendencia = pendenciaRepository.save(pendencia);
        return converterParaDTO(pendencia);
    }

    @Override
    @Transactional
    public PendenciaDTO atualizar(Long id, PendenciaDTO pendenciaDTO) {
        if (!pendenciaRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Pendência não encontrada");
        }
        
        Pendencia pendencia = converterParaEntidade(pendenciaDTO);
        pendencia.setId(id);
        pendencia = pendenciaRepository.save(pendencia);
        return converterParaDTO(pendencia);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        if (!pendenciaRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Pendência não encontrada");
        }
        pendenciaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendenciaDTO> buscarPorConformidade(Long conformidadeId) {
        Conformidade conformidade = conformidadeRepository.findById(conformidadeId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conformidade não encontrada"));
        
        return pendenciaRepository.findByConformidade(conformidade).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendenciaDTO> buscarPorStatus(String resolvida) {
        return pendenciaRepository.findByResolvida(resolvida).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendenciaDTO> buscarPendenciasVencidas(LocalDate data) {
        return pendenciaRepository.findByResolvidaAndPrazoResolucaoBefore("N", data).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    private Pendencia converterParaEntidade(PendenciaDTO dto) {
        Conformidade conformidade = conformidadeRepository.findById(dto.getConformidadeId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conformidade não encontrada"));
        
        return Pendencia.builder()
                .id(dto.getId())
                .conformidade(conformidade)
                .descricaoPendencia(dto.getDescricao())
                .prazoResolucao(dto.getPrazoResolucao())
                .resolvida(dto.getResolvida())
                // Nota: a entidade não possui os campos dataResolucao e observacoes
                .build();
    }

    private PendenciaDTO converterParaDTO(Pendencia pendencia) {
        ConformidadeDTO conformidadeDTO = null;
        if (pendencia.getConformidade() != null) {
            conformidadeDTO = conformidadeService.buscarPorId(pendencia.getConformidade().getId());
        }
        
        return PendenciaDTO.builder()
                .id(pendencia.getId())
                .conformidadeId(pendencia.getConformidade() != null ? pendencia.getConformidade().getId() : null)
                .descricao(pendencia.getDescricaoPendencia())
                .prazoResolucao(pendencia.getPrazoResolucao())
                .resolvida(pendencia.getResolvida())
                // Estes campos não existem na entidade, mas constam no DTO
                .dataResolucao(null)
                .observacoes(null)
                .conformidade(conformidadeDTO)
                .build();
    }
}
