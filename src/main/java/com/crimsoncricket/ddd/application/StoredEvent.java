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

package com.crimsoncricket.ddd.application;

import com.crimsoncricket.ddd.domain.model.DomainEvent;
import org.hibernate.annotations.*;
import org.hibernate.annotations.common.reflection.ClassLoaderDelegate;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.Instant;

import static com.crimsoncricket.asserts.Assert.assertArgumentNotNull;


@Entity
@Immutable
@Table(indexes = {
        @Index(columnList = "occurred_on", unique = false)
})
public class StoredEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @Column(name = "occurred_on")
    private Instant occurredOn;
    private String typeName;

    @Column(columnDefinition="Text")
    private String eventBody;

    protected StoredEvent() {};

    public StoredEvent(Instant occurredOn, String typeName, String eventBody) {
        assertArgumentNotNull(occurredOn, "Timestamp occuredOn may not be null.");
        assertArgumentNotNull(typeName, "Type name may not be null");
        assertArgumentNotNull(eventBody, "Event body may not be null.");
        this.eventBody = eventBody;
        this.occurredOn = occurredOn;
        this.typeName = typeName;
    }

    @SuppressWarnings("unchecked")
    public SequencedEvent toSequencedEvent(EventSerializer serializer)  {
        Class<DomainEvent> eventClass = null;
        try {
            eventClass = (Class<DomainEvent>) Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        DomainEvent event = serializer.unserialize(eventBody, eventClass);
        return new SequencedEvent(eventId, event);
    }


    public Instant occurredOn() {
        return occurredOn;
    }

    public String typeName() {
        return typeName;
    }

    public String eventBody() {
        return eventBody;
    }


}
