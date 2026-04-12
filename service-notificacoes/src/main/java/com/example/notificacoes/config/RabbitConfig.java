package com.example.notificacoes.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "reservas.exchange";
    public static final String ROUTING_KEY_RESERVA_CRIADA = "reserva.criada";
    public static final String QUEUE_RESERVA_CRIADA = "notificacoes.reserva.criada";

    @Bean
    public TopicExchange reservasExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue reservaCriadaQueue() {
        return new Queue(QUEUE_RESERVA_CRIADA, true);
    }

    @Bean
    public Binding reservaCriadaBinding(Queue reservaCriadaQueue, TopicExchange reservasExchange) {
        return BindingBuilder.bind(reservaCriadaQueue)
                .to(reservasExchange)
                .with(ROUTING_KEY_RESERVA_CRIADA);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
