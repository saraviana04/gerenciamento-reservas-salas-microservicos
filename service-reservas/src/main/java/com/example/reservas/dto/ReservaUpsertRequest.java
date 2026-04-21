package com.example.reservas.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservaUpsertRequest(
        @NotNull Long salaId,
        @NotNull Long usuarioId,
        @NotNull @Future LocalDateTime dataReserva
) {
}

