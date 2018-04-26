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

import com.crimsoncricket.ddd.domain.model.Id;
import com.crimsoncricket.ddd.domain.model.TypedIdentity;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class EventData {

	public static EventData from(StoredEvent event) {
		return new EventData(
				event.eventId(),
				event.typeName(),
				event.occurredOn(),
				event.initiator().map(TypedIdentity::toId).orElse(null)
		);
	}

	private Long eventId;

	private String eventName;

	private Instant occurredOn;

	private Id initiator;

	public EventData(
			Long eventId,
			String eventName,
			Instant occurredOn,
			@Nullable Id initiator
	) {
		this.eventId = eventId;
		this.eventName = eventName;
		this.occurredOn = occurredOn;
		this.initiator = initiator;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EventData)) return false;
		EventData eventData = (EventData) o;
		return Objects.equals(eventId, eventData.eventId) &&
				Objects.equals(eventName, eventData.eventName) &&
				Objects.equals(occurredOn, eventData.occurredOn) &&
				Objects.equals(initiator, eventData.initiator);
	}

	public EventData withEventName(String eventName) {
		return new EventData(eventId, eventName, occurredOn, initiator);
	}

	@Override
	public int hashCode() {
		return Objects.hash(eventId, eventName, occurredOn, initiator);
	}

	@Override
	public String toString() {
		return "EventData{" +
				"eventId=" + eventId +
				", eventName='" + eventName + '\'' +
				", occurredOn=" + occurredOn +
				", initiator=" + initiator +
				'}';
	}

	public Long eventId() {
		return eventId;
	}

	public String eventName() {
		return eventName;
	}

	public Instant occurredOn() {
		return occurredOn;
	}

	public Optional<Id> initiator() {
		return Optional.ofNullable(initiator);
	}
}
