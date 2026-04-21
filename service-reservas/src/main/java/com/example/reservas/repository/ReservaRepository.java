package com.example.reservas.repository;

import com.example.reservas.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findBySalaIdAndStatusAndDataReservaBetween(
            Long salaId,
            String status,
            LocalDateTime inicio,
            LocalDateTime fim
    );

    List<Reserva> findByUsuarioIdOrderByDataReservaAsc(Long usuarioId);

    List<Reserva> findByUsuarioIdAndStatusOrderByDataReservaAsc(Long usuarioId, String status);
}
