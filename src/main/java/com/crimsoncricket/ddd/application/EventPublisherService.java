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

package com.crimsoncricket.ddd.application;
import org.springframework.transaction.annotation.Transactional;

import static com.crimsoncricket.asserts.Assert.assertArgumentNotNull;

public class EventPublisherService {


    private String publisherName;

    private EventPublisherRepository eventPublisherRepository;

    private EventStore eventStore;

    private EventSerializer eventSerializer;

    private EventDispatcher eventDispatcher;


    public EventPublisherService(
            String publisherName,
            EventPublisherRepository eventPublisherRepository,
            EventStore eventStore,
            EventSerializer eventSerializer,
            EventDispatcher eventDispatcher

    ) {
        assertArgumentNotNull(publisherName, "Publisher name may not be null");
        this.publisherName = publisherName;

        assertArgumentNotNull(eventPublisherRepository, "Publisher repository may not be null");
        this.eventPublisherRepository = eventPublisherRepository;

        assertArgumentNotNull(eventStore, "Event store may not be null");
        this.eventStore = eventStore;

        assertArgumentNotNull(eventSerializer, "Event serializer may not be null");
        this.eventSerializer = eventSerializer;

        assertArgumentNotNull(eventDispatcher, "Event dispatcher may not be null");
        this.eventDispatcher = eventDispatcher;
    }


    @Transactional
    public void publishAllNonPublishedEvents() {

        EventPublisher publisher = eventPublisherRepository.publisherWithName(this.publisherName);
        if (publisher == null)
            throw new RuntimeException("Event publisher with name " + publisherName + " could not be found.");

        publisher.publishAllUnpublishedEventsFrom(eventStore, eventSerializer, eventDispatcher);

    }
}
