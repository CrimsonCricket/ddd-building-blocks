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
 *
 */

package com.crimsoncricket.ddd.port.adapter.spring.ampqp;

import com.crimsoncricket.asserts.Assert;
import com.crimsoncricket.ddd.application.EventSerializer;
import com.crimsoncricket.ddd.application.SequencedEvent;
import org.springframework.amqp.core.*;


public class EventDispatcher implements com.crimsoncricket.ddd.application.EventDispatcher {


    private EventSerializer eventSerializer;

    private AmqpTemplate amqpTemplate;


    public EventDispatcher(EventSerializer eventSerializer, AmqpTemplate amqpTemplate) {
        Assert.assertArgumentNotNull(eventSerializer, "Event serializer may not be null");
        this.eventSerializer = eventSerializer;
        Assert.assertArgumentNotNull(amqpTemplate, "AMQP template may not be null");
        this.amqpTemplate = amqpTemplate;
    }

    @Override
    public void dispatch(SequencedEvent event) {

        String routingKey = event.domainEvent().getClass().getName();
        String exchangeName = "roep_domain_events";
        String eventBody = eventSerializer.serialize(event.domainEvent());

        Message message = MessageBuilder.withBody(eventBody.getBytes())
                .setContentType("application/json")
                .setHeader("eventId", event.eventId())
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .build();

        this.amqpTemplate.send(exchangeName, routingKey, message);

    }
}
