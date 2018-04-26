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

import java.util.HashSet;
import java.util.Set;

import static com.crimsoncricket.asserts.Assert.assertArgumentNotNull;

public class EventTrackerService {

	private final EventTrackerRepository eventTrackerRepository;

	private final EventStore eventStore;

	private final EventSerializer eventSerializer;

	private final Set<TrackingEventProcessor> processors = new HashSet<>();

	public EventTrackerService(
			EventTrackerRepository eventTrackerRepository,
			EventStore eventStore,
			EventSerializer eventSerializer
	) {

		assertArgumentNotNull(eventTrackerRepository, "Event tracker repository may not be null");
		this.eventTrackerRepository = eventTrackerRepository;

		assertArgumentNotNull(eventStore, "Event store may not be null");
		this.eventStore = eventStore;

		assertArgumentNotNull(eventSerializer, "Event serializer may not be null");
		this.eventSerializer = eventSerializer;

	}

	public EventTrackerService register(TrackingEventProcessor processor) {
		processors.add(processor);
		return this;
	}

	public void processEvents() {
		processors.forEach(this::processEventsWith);
	}

	private void processEventsWith(TrackingEventProcessor processor) {
		boolean finished;
		do {
			finished = processor.processEvents(eventTrackerRepository, eventStore, eventSerializer);
		} while (!finished);
	}
}
