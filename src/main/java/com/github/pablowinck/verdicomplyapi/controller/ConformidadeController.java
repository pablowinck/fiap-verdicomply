package com.github.pablowinck.verdicomplyapi.controller;

import com.github.pablowinck.verdicomplyapi.dto.ConformidadeDTO;
import com.github.pablowinck.verdicomplyapi.service.ConformidadeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/conformidades")
public class ConformidadeController {

    @Autowired
    private ConformidadeService conformidadeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<List<ConformidadeDTO>> listarTodas() {
        return ResponseEntity.ok(conformidadeService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<ConformidadeDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(conformidadeService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<ConformidadeDTO> criar(@Valid @RequestBody ConformidadeDTO conformidadeDTO) {
        ConformidadeDTO conformidadeCriada = conformidadeService.criar(conformidadeDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(conformidadeCriada.getId()).toUri();
        return ResponseEntity.created(uri).body(conformidadeCriada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<ConformidadeDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ConformidadeDTO conformidadeDTO) {
        return ResponseEntity.ok(conformidadeService.atualizar(id, conformidadeDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        conformidadeService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/auditoria/{auditoriaId}")
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<List<ConformidadeDTO>> buscarPorAuditoria(@PathVariable Long auditoriaId) {
        return ResponseEntity.ok(conformidadeService.buscarPorAuditoria(auditoriaId));
    }

    @GetMapping("/status/{estaConforme}")
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<List<ConformidadeDTO>> buscarPorStatus(@PathVariable String estaConforme) {
        return ResponseEntity.ok(conformidadeService.buscarPorStatus(estaConforme));
    }

    @GetMapping("/norma/{normaAmbientalId}")
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<List<ConformidadeDTO>> buscarPorNorma(@PathVariable Long normaAmbientalId) {
        return ResponseEntity.ok(conformidadeService.buscarPorNorma(normaAmbientalId));
    }
}
