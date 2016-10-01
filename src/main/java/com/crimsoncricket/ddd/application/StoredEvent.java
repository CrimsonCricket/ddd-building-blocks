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
import com.crimsoncricket.ddd.domain.model.TypedIdentity;
import org.hibernate.annotations.*;
import org.hibernate.annotations.common.reflection.ClassLoaderDelegate;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.crimsoncricket.asserts.Assert.assertArgumentCollectionNotContainsNull;
import static com.crimsoncricket.asserts.Assert.assertArgumentNotNull;


@Entity
@Immutable
@Table(indexes = {
        @Index(columnList = "occurred_on")
})
public class StoredEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @Column(name = "occurred_on", nullable = false)
    private Instant occurredOn;

    private Long occurredOnInEpochMillis;

    @Column(nullable = false)
    private String typeName;

    @Column(columnDefinition="Text", nullable = false)
    private String eventBody;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "identityType", column = @Column(name = "initiatorType")),
            @AttributeOverride(name = "identityValue", column = @Column(name = "initiatorId"))
    })
    private TypedIdentity initiator;

    @ElementCollection
    @JoinTable(indexes = {
            @Index(columnList = "StoredEvent_eventId"),
            @Index(columnList = "identityType,identityValue"),
    })
    private Set<TypedIdentity> eventReferences = new HashSet<>();

    protected StoredEvent() {};

    public StoredEvent(
            Instant occurredOn,
            String typeName,
            String eventBody,
            @Nullable TypedIdentity initiator,
            Set<TypedIdentity> eventReferences

    ) {
        assertArgumentNotNull(occurredOn, "Timestamp occuredOn may not be null.");
        assertArgumentNotNull(typeName, "Type name may not be null");
        assertArgumentNotNull(eventBody, "Event body may not be null.");
        assertArgumentCollectionNotContainsNull(eventReferences, "Event references collection may not contain nulls");
        this.eventBody = eventBody;
        this.occurredOn = occurredOn;
        this.occurredOnInEpochMillis = occurredOn.toEpochMilli();
        this.typeName = typeName;
        this.initiator = initiator;
        this.eventReferences.addAll(eventReferences);
    }

    @SuppressWarnings({"unchecked", "WeakerAccess"})
    public SequencedEvent toSequencedEvent(EventSerializer serializer)  {
        Class<DomainEvent> eventClass;
        try {
            eventClass = (Class<DomainEvent>) Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        DomainEvent event = serializer.unserialize(eventBody, eventClass);
        return new SequencedEvent(eventId, event);
    }

    public Instant occurredOn() {
        if (occurredOnInEpochMillis != null)
            return Instant.ofEpochMilli(occurredOnInEpochMillis);
        else
            return occurredOn;
    }

    public String typeName() {
        return typeName;
    }

    public String eventBody() {
        return eventBody;
    }

    public Optional<TypedIdentity> initiator() {
        return Optional.ofNullable(initiator);
    }

    public Set<TypedIdentity> eventReferences() {
        return Collections.unmodifiableSet(eventReferences);
    }
}
