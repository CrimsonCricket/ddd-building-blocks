/*
 * Copyright 2016 Martijn van der Woud - The Crimson Cricket Internet Services
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.crimsoncricket.ddd.port.adapter.spring.ampqp;

import com.crimsoncricket.ddd.application.EventMessage;
import com.crimsoncricket.ddd.application.EventProcessor;
import com.crimsoncricket.ddd.application.EventSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;

import static com.crimsoncricket.asserts.Assert.assertArgumentNotNull;

public class AMQPPublishingEventProcessor implements EventProcessor {

	private static final Logger logger = LoggerFactory.getLogger(AMQPPublishingEventProcessor.class);

	private EventSerializer eventSerializer;

	private AmqpTemplate amqpTemplate;

	private String exchangeName;

	public AMQPPublishingEventProcessor(
			EventSerializer eventSerializer,
			AmqpTemplate amqpTemplate,
			String exchangeName
	) {
		assertArgumentNotNull(eventSerializer, "Event serializer may not be null");
		this.eventSerializer = eventSerializer;
		assertArgumentNotNull(amqpTemplate, "AMQP template may not be null");
		this.amqpTemplate = amqpTemplate;
		assertArgumentNotNull(exchangeName, "Exchange name may not be null");
		this.exchangeName = exchangeName;
	}

	@Override
	public void process(EventMessage eventMessage) {
		String routingKey = eventMessage.event().getClass().getName();
		String eventBody = eventSerializer.serialize(eventMessage.event());
		Message message = MessageBuilder.withBody(eventBody.getBytes())
				.setContentType("application/json")
				.setHeader("eventId", eventMessage.eventData().eventId())
				.setDeliveryMode(MessageDeliveryMode.PERSISTENT)
				.build();

		this.amqpTemplate.send(exchangeName, routingKey, message);
		logger.info("Published event message with routing key " + routingKey);
	}

}
