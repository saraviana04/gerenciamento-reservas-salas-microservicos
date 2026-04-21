package com.example.reservas.controller;

import com.example.reservas.dto.ReservaUpsertRequest;
import com.example.reservas.model.Reserva;
import com.example.reservas.repository.ReservaRepository;
import com.example.reservas.service.ReservaEventPublisher;
import com.example.reservas.service.ReservaRules;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaRepository reservaRepository;
    private final ReservaEventPublisher reservaEventPublisher;
    private final ReservaRules reservaRules;

    public ReservaController(ReservaRepository reservaRepository,
                             ReservaEventPublisher reservaEventPublisher,
                             ReservaRules reservaRules) {
        this.reservaRepository = reservaRepository;
        this.reservaEventPublisher = reservaEventPublisher;
        this.reservaRules = reservaRules;
    }

    @PostMapping
    public ResponseEntity<Reserva> criar(@Valid @RequestBody ReservaUpsertRequest request) {
        Reserva reserva = new Reserva();
        reserva.setSalaId(request.salaId());
        reserva.setUsuarioId(request.usuarioId());
        reserva.setDataReserva(request.dataReserva());
        reserva.setDuracaoHoras(Reserva.DURACAO_PADRAO_HORAS);
        reserva.setStatus(Reserva.STATUS_ATIVA);

        LocalDateTime inicio = reserva.getDataReserva();
        LocalDateTime fim = inicio.plusHours(Reserva.DURACAO_PADRAO_HORAS);
        if (!reservaRules.salaEstaDisponivel(reserva.getSalaId(), inicio, fim)) {
            return ResponseEntity.status(409).build();
        }

        Reserva salva = reservaRepository.save(reserva);
        reservaEventPublisher.publicarReservaCriada(salva);
        return ResponseEntity.ok(salva);
    }

    @GetMapping
    public ResponseEntity<List<Reserva>> listar() {
        return ResponseEntity.ok(reservaRepository.findAll());
    }

    @GetMapping(params = "usuarioId")
    public ResponseEntity<List<Reserva>> listarPorUsuario(@RequestParam Long usuarioId,
                                                          @RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            return ResponseEntity.ok(reservaRepository.findByUsuarioIdAndStatusOrderByDataReservaAsc(usuarioId, status));
        }
        return ResponseEntity.ok(reservaRepository.findByUsuarioIdOrderByDataReservaAsc(usuarioId));
    }

    @GetMapping(params = {"salaId", "data"})
    public ResponseEntity<List<Reserva>> listarPorSalaEData(@RequestParam Long salaId, @RequestParam String data) {
        LocalDate dia = LocalDate.parse(data);
        LocalDateTime inicio = dia.atStartOfDay();
        LocalDateTime fim = dia.atTime(LocalTime.MAX);
        return ResponseEntity.ok(reservaRepository.findBySalaIdAndStatusAndDataReservaBetween(
                salaId,
                Reserva.STATUS_ATIVA,
                inicio,
                fim
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obter(@PathVariable Long id) {
        return reservaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reserva> atualizar(@PathVariable Long id, @Valid @RequestBody ReservaUpsertRequest nova) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    reserva.setSalaId(nova.salaId());
                    reserva.setUsuarioId(nova.usuarioId());
                    reserva.setDataReserva(nova.dataReserva());
                    reserva.setDuracaoHoras(Reserva.DURACAO_PADRAO_HORAS);
                    reserva.setStatus(Reserva.STATUS_ATIVA);

                    LocalDateTime inicio = reserva.getDataReserva();
                    LocalDateTime fim = inicio.plusHours(Reserva.DURACAO_PADRAO_HORAS);
                    if (!reservaRules.salaEstaDisponivel(reserva.getSalaId(), inicio, fim, reserva.getId())) {
                        return ResponseEntity.status(409).build();
                    }

                    return ResponseEntity.ok(reservaRepository.save(reserva));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        Optional<Reserva> reserva = reservaRepository.findById(id);
        if (reserva.isEmpty()) return ResponseEntity.notFound().build();

        Reserva r = reserva.get();
        r.setStatus(Reserva.STATUS_CANCELADA);
        reservaRepository.save(r);
        return ResponseEntity.noContent().build();
    }
}
