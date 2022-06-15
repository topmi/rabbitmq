package com.roy.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(Sink.class)
public class MessageReceiver {
	private Logger logger = LoggerFactory.getLogger(MessageReceiver.class);

	@EventListener
	@StreamListener(Sink.INPUT)
	public void process(Object message) {
        System.out.println("received message : " + message);
        logger.info("received message : {}", message);
    }
}
