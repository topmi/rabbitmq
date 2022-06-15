package com.roy.rabbitmq.sharding;

import com.rabbitmq.client.*;
import com.roy.rabbitmq.RabbitMQUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author roy
 * @desc 需要先启用Sharing插件，并配置sharding策略。
 */
public class ShardingConsumer {
    public static final String QUEUENAME="sharding.fashiondna";
    public static void main(String[] args) throws Exception {
        Connection connection = RabbitMQUtil.getConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUENAME,true,false,false,null);

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
        //三个分片就需要消费三次。
        //sharding插件的实现原理就是将basicConsume方法绑定到分片队列中连接最少的一个队列上。
        String consumeerFlag1 = channel.basicConsume(QUEUENAME, true, myconsumer);
        System.out.println("c1:"+consumeerFlag1);
        String consumeerFlag2 = channel.basicConsume(QUEUENAME, true, myconsumer);
        System.out.println("c2:"+consumeerFlag2);
        String consumeerFlag3 = channel.basicConsume(QUEUENAME, true, myconsumer);
        System.out.println("c3:"+consumeerFlag3);
    }
}
