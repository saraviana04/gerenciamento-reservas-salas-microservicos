package com.example.salas.controller;

import com.example.salas.model.Sala;
import com.example.salas.repository.SalaRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salas")
public class SalaController {

    private final SalaRepository salaRepository;

    public SalaController(SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    @PostMapping
    public ResponseEntity<Sala> criar(@Valid @RequestBody Sala sala) {
        return ResponseEntity.ok(salaRepository.save(sala));
    }

    @GetMapping
    public ResponseEntity<List<Sala>> listar() {
        return ResponseEntity.ok(salaRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sala> obter(@PathVariable Long id) {
        return salaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sala> atualizar(@PathVariable Long id, @Valid @RequestBody Sala nova) {
        return salaRepository.findById(id)
                .map(sala -> {
                    sala.setNome(nova.getNome());
                    sala.setCapacidade(nova.getCapacidade());
                    sala.setDescricao(nova.getDescricao());
                    return ResponseEntity.ok(salaRepository.save(sala));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (salaRepository.existsById(id)) {
            salaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
