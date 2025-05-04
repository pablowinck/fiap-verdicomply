package com.github.pablowinck.verdicomplyapi.controller;

import com.github.pablowinck.verdicomplyapi.dto.AuditoriaDTO;
import com.github.pablowinck.verdicomplyapi.service.AuditoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditorias")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditoriaDTO>> listarTodas() {
        return ResponseEntity.ok(auditoriaService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<AuditoriaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(auditoriaService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('GESTOR', 'ADMIN')")
    public ResponseEntity<AuditoriaDTO> criar(@Valid @RequestBody AuditoriaDTO auditoriaDTO) {
        AuditoriaDTO auditoriaCriada = auditoriaService.criar(auditoriaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(auditoriaCriada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR', 'ADMIN')")
    public ResponseEntity<AuditoriaDTO> atualizar(@PathVariable Long id, @Valid @RequestBody AuditoriaDTO auditoriaDTO) {
        return ResponseEntity.ok(auditoriaService.atualizar(id, auditoriaDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        auditoriaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<List<AuditoriaDTO>> buscarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(auditoriaService.buscarPorStatus(status));
    }

    @GetMapping("/departamento/{departamentoId}")
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<List<AuditoriaDTO>> buscarPorDepartamento(@PathVariable Long departamentoId) {
        return ResponseEntity.ok(auditoriaService.buscarPorDepartamento(departamentoId));
    }
}
