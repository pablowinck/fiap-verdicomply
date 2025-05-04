package com.github.pablowinck.verdicomplyapi.controller;

import com.github.pablowinck.verdicomplyapi.dto.LogConformidadeDTO;
import com.github.pablowinck.verdicomplyapi.service.LogConformidadeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogConformidadeController {

    @Autowired
    private LogConformidadeService logConformidadeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR', 'ADMIN')")
    public ResponseEntity<List<LogConformidadeDTO>> listarTodos() {
        return ResponseEntity.ok(logConformidadeService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR', 'ADMIN')")
    public ResponseEntity<LogConformidadeDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(logConformidadeService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<LogConformidadeDTO> criar(@Valid @RequestBody LogConformidadeDTO logConformidadeDTO) {
        LogConformidadeDTO logCriado = logConformidadeService.criar(logConformidadeDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(logCriado.getId()).toUri();
        return ResponseEntity.created(uri).body(logCriado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        logConformidadeService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/conformidade/{conformidadeId}")
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<List<LogConformidadeDTO>> buscarPorConformidade(@PathVariable Long conformidadeId) {
        return ResponseEntity.ok(logConformidadeService.buscarPorConformidade(conformidadeId));
    }

    @GetMapping("/acao/{acao}")
    @PreAuthorize("hasAnyRole('GESTOR', 'ADMIN')")
    public ResponseEntity<List<LogConformidadeDTO>> buscarPorAcao(@PathVariable String acao) {
        return ResponseEntity.ok(logConformidadeService.buscarPorAcao(acao));
    }
}
