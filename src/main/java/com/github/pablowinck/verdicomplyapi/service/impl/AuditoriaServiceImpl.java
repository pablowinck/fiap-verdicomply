package com.github.pablowinck.verdicomplyapi.service.impl;

import com.github.pablowinck.verdicomplyapi.dto.AuditoriaDTO;
import com.github.pablowinck.verdicomplyapi.model.Auditoria;
import com.github.pablowinck.verdicomplyapi.model.Departamento;
import com.github.pablowinck.verdicomplyapi.repository.AuditoriaRepository;
import com.github.pablowinck.verdicomplyapi.repository.DepartamentoRepository;
import com.github.pablowinck.verdicomplyapi.service.AuditoriaService;
import com.github.pablowinck.verdicomplyapi.service.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final DepartamentoRepository departamentoRepository;

    @Override
    public List<AuditoriaDTO> listarTodas() {
        return auditoriaRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AuditoriaDTO buscarPorId(Long id) {
        Auditoria auditoria = auditoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Auditoria não encontrada"));
        return converterParaDTO(auditoria);
    }

    @Override
    @Transactional
    public AuditoriaDTO criar(AuditoriaDTO auditoriaDTO) {
        Departamento departamento = departamentoRepository.findById(auditoriaDTO.getDepartamentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Departamento não encontrado"));

        Auditoria auditoria = new Auditoria();
        auditoria.setDepartamento(departamento);
        auditoria.setDataAuditoria(auditoriaDTO.getDataAuditoria());
        auditoria.setAuditorResponsavel(auditoriaDTO.getAuditorResponsavel());
        auditoria.setStatusAuditoria(auditoriaDTO.getStatusAuditoria());

        auditoria = auditoriaRepository.save(auditoria);
        return converterParaDTO(auditoria);
    }

    @Override
    @Transactional
    public AuditoriaDTO atualizar(Long id, AuditoriaDTO auditoriaDTO) {
        Auditoria auditoria = auditoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Auditoria não encontrada"));

        Departamento departamento = departamentoRepository.findById(auditoriaDTO.getDepartamentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Departamento não encontrado"));

        auditoria.setDepartamento(departamento);
        auditoria.setDataAuditoria(auditoriaDTO.getDataAuditoria());
        auditoria.setAuditorResponsavel(auditoriaDTO.getAuditorResponsavel());
        auditoria.setStatusAuditoria(auditoriaDTO.getStatusAuditoria());

        auditoria = auditoriaRepository.save(auditoria);
        return converterParaDTO(auditoria);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        if (!auditoriaRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Auditoria não encontrada");
        }
        auditoriaRepository.deleteById(id);
    }

    @Override
    public List<AuditoriaDTO> buscarPorStatus(String status) {
        return auditoriaRepository.findByStatusAuditoriaContainingIgnoreCase(status).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditoriaDTO> buscarPorDepartamento(Long departamentoId) {
        Departamento departamento = departamentoRepository.findById(departamentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Departamento não encontrado"));

        return auditoriaRepository.findByDepartamento(departamento).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    private AuditoriaDTO converterParaDTO(Auditoria auditoria) {
        AuditoriaDTO dto = new AuditoriaDTO();
        dto.setId(auditoria.getId());
        dto.setDepartamentoId(auditoria.getDepartamento().getId());
        dto.setDataAuditoria(auditoria.getDataAuditoria());
        dto.setAuditorResponsavel(auditoria.getAuditorResponsavel());
        dto.setStatusAuditoria(auditoria.getStatusAuditoria());
        return dto;
    }
}
