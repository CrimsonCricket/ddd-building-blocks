/*
 * Copyright 2015 Martijn van der Woud - The Crimson Cricket Internet Services
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

package com.crimsoncricket.ddd.port.adapter.jpa;

import com.crimsoncricket.ddd.application.EventSerializer;
import com.crimsoncricket.ddd.application.EventStore;
import com.crimsoncricket.ddd.application.StoredEvent;
import com.crimsoncricket.ddd.domain.model.DomainEvent;
import org.springframework.stereotype.Repository;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.crimsoncricket.asserts.Assert.assertArgumentNotNull;

@Repository
public class JpaEventStore implements EventStore {

    @PersistenceContext
    private EntityManager entityManager;

    private EventSerializer eventSerializer;

    public JpaEventStore(EventSerializer eventSerializer) {
        assertArgumentNotNull(eventSerializer, "Event serializer may not be null.");
        this.eventSerializer = eventSerializer;
    }

    @Override
    public void append(DomainEvent anEvent) {
        assertArgumentNotNull(anEvent, "The domain event may not be null.");
        StoredEvent storedEvent = createStoredEvent(anEvent);
        entityManager.persist(storedEvent);
    }

    private StoredEvent createStoredEvent(DomainEvent anEvent) {
        String serializedEvent = eventSerializer.serialize(anEvent);
        return new StoredEvent(anEvent.occurredOn(), anEvent.getClass().getName(), serializedEvent);
    }


}