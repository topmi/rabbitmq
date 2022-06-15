package com.roy.rabbitmq.federation;

import com.rabbitmq.client.*;
import com.roy.rabbitmq.RabbitMQUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author roy
 * @desc 下游服务通过Federation同步到Upsteram的消息，并消费。
 */
public class DownStreamConsumer {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("mendd.com");
        factory.setPort(5672);
        factory.setUsername("mq_mendd");
        factory.setPassword("0CEF42D2E3D69BCDD785");
        factory.setVirtualHost("/fashiondna");
        Connection connection = factory.newConnection();
//        Connection connection = RabbitMQUtil.getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("federation.fashiondna.exchange","direct");
        channel.queueDeclare("federation.fashiondna.queue",true,false,false,null);
        channel.queueBind("federation.fashiondna.queue","federation.fashiondna.exchange","federation.fashiondna.routKey");

        Consumer myconsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                System.out.println("========================");
                String routingKey = envelope.getRoutingKey();
                System.out.println("routingKey >" + routingKey);
                String contentType = properties.getContentType();
                System.out.println("contentType >" + contentType);
                long deliveryTag = envelope.getDeliveryTag();
                System.out.println("deliveryTag >" + deliveryTag);
                System.out.println("content:" + new String(body, "UTF-8"));
                // (process the message components here ...)
                //消息处理完后，进行答复。答复过的消息，服务器就不会再次转发。
                //没有答复过的消息，服务器会一直不停转发。
//				 channel.basicAck(deliveryTag, false);
            }
        };
        channel.basicConsume("federation.fashiondna.queue", true, myconsumer);
    }
}
