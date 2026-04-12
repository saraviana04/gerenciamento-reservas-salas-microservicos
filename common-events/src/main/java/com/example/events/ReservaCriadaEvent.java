package com.example.events;

import java.time.LocalDateTime;

public record ReservaCriadaEvent(
        Long reservaId,
        Long salaId,
        Long usuarioId,
        LocalDateTime dataReserva,
        Integer duracaoHoras,
        String status
) {
}
