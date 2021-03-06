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

import java.util.Optional;

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
		Optional<TypedIdentity> initiator = initiatorResolver.initiatorOf(anEvent);
		return new StoredEvent(
				anEvent.occurredOn(),
				anEvent.getClass().getName(),
				serializedEvent,
				initiator.orElse(null)
		);
	}

	protected abstract void store(StoredEvent storedEvent);

}
