package com.roy.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(Source.class)
public class MessageSender {

	@Autowired
	private Source source;
	
	public void sendMessage(Object message) {
		MessageBuilder<Object> builder = MessageBuilder.withPayload(message);
		source.output().send(builder.build());
	}
}
