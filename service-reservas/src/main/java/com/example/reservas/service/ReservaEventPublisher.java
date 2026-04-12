package com.example.reservas.service;

import com.example.events.ReservaCriadaEvent;
import com.example.reservas.config.RabbitConfig;
import com.example.reservas.model.Reserva;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReservaEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public ReservaEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarReservaCriada(Reserva reserva) {
        ReservaCriadaEvent event = new ReservaCriadaEvent(
                reserva.getId(),
                reserva.getSalaId(),
                reserva.getUsuarioId(),
                reserva.getDataReserva(),
                reserva.getDuracaoHoras(),
                reserva.getStatus()
        );
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY_RESERVA_CRIADA,
                event
        );
    }
}
