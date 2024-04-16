package backend.backend.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import backend.backend.models.Message;

@Service
public class MessageSender {

    @Value("${rabbitmq.topic.exchange}")
    private String topicExchangeName;
    
    @Value("${rabbitmq.routing.key}")
    private String routingKey;


    @Autowired
    private RabbitTemplate template;

    public void sendMessage(Message message) {
        template.convertAndSend(topicExchangeName, routingKey, message.toJson().toString());
    }
}