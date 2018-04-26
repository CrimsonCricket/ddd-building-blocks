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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class EventTracker {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long trackerId;

	private String trackerName;

	private Long latestTrackedEventId;

	protected EventTracker() {
	}

	public EventTracker(String trackerName) {
		this.trackerName = trackerName;
		this.latestTrackedEventId = 0L;
	}

	boolean processEvents(
			EventStore eventStore,
			EventSerializer eventSerializer,
			EventProcessor eventProcessor
	) {
		Long maxEvents = 500L;
		List<EventMessage> messages = eventStore
				.maxEventsAfter(latestTrackedEventId, maxEvents)
				.stream()
				.map(storedEvent -> EventMessage.from(storedEvent, eventSerializer))
				.collect(Collectors.toList());

		messages.forEach(message -> {
			eventProcessor.process(message);
			latestTrackedEventId = message.eventData().eventId();
		});
		return messages.size() < maxEvents;
	}

}
