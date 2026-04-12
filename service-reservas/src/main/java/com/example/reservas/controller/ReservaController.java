package com.example.reservas.controller;

import com.example.reservas.model.Reserva;
import com.example.reservas.repository.ReservaRepository;
import com.example.reservas.service.ReservaEventPublisher;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaRepository reservaRepository;
    private final ReservaEventPublisher reservaEventPublisher;

    public ReservaController(ReservaRepository reservaRepository, ReservaEventPublisher reservaEventPublisher) {
        this.reservaRepository = reservaRepository;
        this.reservaEventPublisher = reservaEventPublisher;
    }

    @PostMapping
    public ResponseEntity<Reserva> criar(@Valid @RequestBody Reserva reserva) {
        Reserva salva = reservaRepository.save(reserva);
        reservaEventPublisher.publicarReservaCriada(salva);
        return ResponseEntity.ok(salva);
    }

    @GetMapping
    public ResponseEntity<List<Reserva>> listar() {
        return ResponseEntity.ok(reservaRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obter(@PathVariable Long id) {
        return reservaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reserva> atualizar(@PathVariable Long id, @Valid @RequestBody Reserva nova) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    reserva.setSalaId(nova.getSalaId());
                    reserva.setUsuarioId(nova.getUsuarioId());
                    reserva.setDataReserva(nova.getDataReserva());
                    reserva.setDuracaoHoras(nova.getDuracaoHoras());
                    reserva.setStatus(nova.getStatus());
                    return ResponseEntity.ok(reservaRepository.save(reserva));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (reservaRepository.existsById(id)) {
            reservaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
