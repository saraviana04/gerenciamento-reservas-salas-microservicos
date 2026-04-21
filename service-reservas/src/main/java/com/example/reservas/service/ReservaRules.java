package com.example.reservas.service;

import com.example.reservas.model.Reserva;
import com.example.reservas.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservaRules {

    private final ReservaRepository reservaRepository;

    public ReservaRules(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public boolean salaEstaDisponivel(Long salaId, LocalDateTime inicio, LocalDateTime fim, Long excluirReservaId) {
        LocalDateTime inicioDoDia = LocalDate.of(inicio.getYear(), inicio.getMonth(), inicio.getDayOfMonth())
                .atStartOfDay();
        LocalDateTime fimDoDia = LocalDate.of(inicio.getYear(), inicio.getMonth(), inicio.getDayOfMonth())
                .atTime(LocalTime.MAX);

        List<Reserva> doDia = reservaRepository.findBySalaIdAndStatusAndDataReservaBetween(
                salaId,
                Reserva.STATUS_ATIVA,
                inicioDoDia,
                fimDoDia
        );

        for (Reserva existente : doDia) {
            if (excluirReservaId != null && excluirReservaId.equals(existente.getId())) continue;
            LocalDateTime existenteInicio = existente.getDataReserva();
            LocalDateTime existenteFim = existente.getDataFim();
            if (existenteInicio == null || existenteFim == null) continue;

            boolean sobrepoe = existenteInicio.isBefore(fim) && existenteFim.isAfter(inicio);
            if (sobrepoe) return false;
        }

        return true;
    }

    public boolean salaEstaDisponivel(Long salaId, LocalDateTime inicio, LocalDateTime fim) {
        return salaEstaDisponivel(salaId, inicio, fim, null);
    }
}
