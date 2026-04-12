package com.example.reservas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
@Data
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long salaId;

    @NotNull
    @Column(nullable = false)
    private Long usuarioId;

    @NotNull
    @Future
    @Column(nullable = false)
    private LocalDateTime dataReserva;

    @NotNull
    @Min(1)
    @Max(8)
    @Column(nullable = false)
    private Integer duracaoHoras;

    @NotBlank
    @Pattern(regexp = "PENDENTE|APROVADA|REJEITADA")
    @Column(nullable = false)
    private String status;
}
