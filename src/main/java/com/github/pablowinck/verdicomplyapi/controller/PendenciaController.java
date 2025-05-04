package com.github.pablowinck.verdicomplyapi.controller;

import com.github.pablowinck.verdicomplyapi.dto.PendenciaDTO;
import com.github.pablowinck.verdicomplyapi.service.PendenciaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pendencias")
public class PendenciaController {

    @Autowired
    private PendenciaService pendenciaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<List<PendenciaDTO>> listarTodas() {
        return ResponseEntity.ok(pendenciaService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<PendenciaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pendenciaService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<PendenciaDTO> criar(@Valid @RequestBody PendenciaDTO pendenciaDTO) {
        PendenciaDTO pendenciaCriada = pendenciaService.criar(pendenciaDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(pendenciaCriada.getId()).toUri();
        return ResponseEntity.created(uri).body(pendenciaCriada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<PendenciaDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PendenciaDTO pendenciaDTO) {
        return ResponseEntity.ok(pendenciaService.atualizar(id, pendenciaDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        pendenciaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/conformidade/{conformidadeId}")
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<List<PendenciaDTO>> buscarPorConformidade(@PathVariable Long conformidadeId) {
        return ResponseEntity.ok(pendenciaService.buscarPorConformidade(conformidadeId));
    }

    @GetMapping("/status/{resolvida}")
    @PreAuthorize("hasAnyRole('AUDITOR', 'GESTOR', 'ADMIN')")
    public ResponseEntity<List<PendenciaDTO>> buscarPorStatus(@PathVariable String resolvida) {
        return ResponseEntity.ok(pendenciaService.buscarPorStatus(resolvida));
    }

    @GetMapping("/vencidas")
    @PreAuthorize("hasAnyRole('GESTOR', 'ADMIN')")
    public ResponseEntity<List<PendenciaDTO>> buscarPendenciasVencidas(
            @RequestParam(value = "data", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        
        if (data == null) {
            data = LocalDate.now();
        }
        
        return ResponseEntity.ok(pendenciaService.buscarPendenciasVencidas(data));
    }
}
