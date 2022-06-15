package com.roy.rabbitmq.direct;

import com.rabbitmq.client.*;
import com.roy.rabbitmq.RabbitMQUtil;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Sender {

	public static final String QUEUE_NAME_classic = "hello-classic";
	public static final String QUEUE_NAME_quorum = "hello-quorum";
	public static final String QUEUE_NAME_stream = "hello-stream";

	public static final String QUEUE_NAME = QUEUE_NAME_stream;

	public static void main(String[] args) throws Exception {
		Connection connection = RabbitMQUtil.getConnection();
		Channel channel = connection.createChannel();

		AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
		builder.deliveryMode(MessageProperties.PERSISTENT_TEXT_PLAIN.getDeliveryMode());
		builder.priority(MessageProperties.PERSISTENT_TEXT_PLAIN.getPriority());
		//携带消息ID
		builder.messageId(""+channel.getNextPublishSeqNo());
		Map<String, Object> headers = new HashMap<>();
		//携带订单号
		headers.put("order", "123");
		builder.headers(headers);

		if (QUEUE_NAME.equals(QUEUE_NAME_classic)) {
			classicTest(channel);
		}
		if (QUEUE_NAME.equals(QUEUE_NAME_quorum)) {
			quorumTest(channel);
		}
		if (QUEUE_NAME.equals(QUEUE_NAME_stream)) {
			streamTest(channel);
		}

		String message = new Date() + " Hello World!" + QUEUE_NAME;

		channel.basicPublish("", QUEUE_NAME, builder.build(), message.getBytes("UTF-8"));
		System.out.println(" [x] Sent '" + message + "'");

		channel.close();
		connection.close();
	}

	public static void classicTest(Channel channel) throws IOException {
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
	}

	public static void quorumTest(Channel channel) throws IOException {
		////声明Quorum队列的方式就是添加一个x-queue-type参数，指定为quorum。默认是classic
		Map<String,Object> params = new HashMap<>();
		params.put("x-queue-type","quorum");
		channel.queueDeclare(QUEUE_NAME, true, false, false, params);
	}

	public static void streamTest(Channel channel) throws IOException {
		//声明Stream队列的方式。
		Map<String,Object> params = new HashMap<>();
		params.put("x-queue-type","stream");
		params.put("x-max-length-bytes", 20_000_000_000L); // maximum stream size: 20 GB
		params.put("x-stream-max-segment-size-bytes", 100_000_000); // size of segment files: 100 MB
		channel.queueDeclare(QUEUE_NAME, true, false, false, params);
	}
}
