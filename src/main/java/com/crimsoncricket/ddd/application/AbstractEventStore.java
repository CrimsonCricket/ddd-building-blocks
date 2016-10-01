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

package com.crimsoncricket.ddd.application;

import com.crimsoncricket.ddd.domain.model.DomainEvent;
import com.crimsoncricket.ddd.domain.model.Id;
import com.crimsoncricket.ddd.domain.model.TypedIdentity;
import com.crimsoncricket.ddd.domain.model.ValueObject;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.*;

import static com.crimsoncricket.asserts.Assert.assertArgumentNotNull;

public abstract class AbstractEventStore implements EventStore {

    private final EventSerializer eventSerializer;
    private final EventInitiatorResolver initiatorResolver;

    protected AbstractEventStore(EventSerializer eventSerializer, EventInitiatorResolver initiatorResolver) {
        assertArgumentNotNull(eventSerializer, "Event serializer may not be null");
        assertArgumentNotNull(initiatorResolver, "Initiator resolver may not be null");
        this.eventSerializer = eventSerializer;
        this.initiatorResolver = initiatorResolver;
    }

    @Override
    public void append(DomainEvent anEvent) {
        StoredEvent storedEvent = storedEventFrom(anEvent);
        store(storedEvent);
    }


    private StoredEvent storedEventFrom(DomainEvent anEvent) {
        String serializedEvent = eventSerializer.serialize(anEvent);
        Optional<TypedIdentity> initiator = initiatorResolver.intiatorOf(anEvent);
        return new StoredEvent(
                anEvent.occurredOn(),
                anEvent.getClass().getName(),
                serializedEvent,
                initiator.orElse(null),
                eventReferencesFrom(anEvent)
        );
    }

    private Set<TypedIdentity> eventReferencesFrom(Object anEvent) {
        Set<TypedIdentity> references = new HashSet<>();

        new HashMap<>();
        Class<?> eventClass = anEvent.getClass();

        List<Field> fields = FieldUtils.getAllFieldsList(eventClass);
        fields.forEach(field -> {
            try {
                Object value = FieldUtils.readField(field, anEvent, true);
                if (value instanceof Id) {
                    references.add(((Id) value).asTypedIdentity());

                } else if (value instanceof Iterable) {
                    ((Iterable<?>) value).forEach(item ->{
                        references.addAll(eventReferencesFrom(item));
                    });

                } else if (value instanceof Map) {
                    ((Map<?,?>) value).forEach((key, item) -> {

                        if (key instanceof Id)
                            references.add(((Id) key).asTypedIdentity());

                        references.addAll(eventReferencesFrom(item));
                    });
                } else if (value instanceof ValueObject) {
                    references.addAll( eventReferencesFrom(value));
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });



        return references;
    }

    protected abstract void store(StoredEvent storedEvent);


}
