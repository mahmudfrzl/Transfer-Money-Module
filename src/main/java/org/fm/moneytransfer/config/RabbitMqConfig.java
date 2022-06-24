package org.fm.moneytransfer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import java.util.List;

@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.queue}")
    private String queueName;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Queue firstQueue(){
        return new Queue(queueName,true);
    }


    @Bean
    public Queue secondQueue(){
        return new Queue("transfer-step-2",true);
    }


    @Bean
    public Queue thirdQueue(){
        return new Queue("transfer-step-3",true);
    }

    @Bean
    public Binding firstBinding(Queue firstQueue,DirectExchange exchange){
        return BindingBuilder.bind(firstQueue).to(exchange).with(routingKey);
    }

    @Bean
    public Binding secondBinding(Queue secondQueue, DirectExchange exchange){
        return BindingBuilder.bind(secondQueue).to(exchange).with("second-route");
    }

    @Bean
    public Binding thirdBinding(Queue thirdQueue, DirectExchange exchange){
        return BindingBuilder.bind(thirdQueue).to(exchange).with("third-route");
    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

}
