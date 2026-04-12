package com.example.notificacoes.consumer;

import com.example.events.ReservaCriadaEvent;
import com.example.notificacoes.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ReservaCriadaListener {

    private static final Logger logger = LoggerFactory.getLogger(ReservaCriadaListener.class);

    @RabbitListener(queues = RabbitConfig.QUEUE_RESERVA_CRIADA)
    public void onReservaCriada(ReservaCriadaEvent event) {
        logger.info("Reserva criada recebida: id={}, salaId={}, usuarioId={}, data={}, duracao={}, status={}",
                event.reservaId(),
                event.salaId(),
                event.usuarioId(),
                event.dataReserva(),
                event.duracaoHoras(),
                event.status()
        );
    }
}
