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

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.crimsoncricket.asserts.Assert.assertArgumentNotNull;

@SuppressWarnings("SpringJavaAutowiringInspection")
public class EventPublisherService {


	private final String publisherName;
	private final EventPublisherRepository eventPublisherRepository;
	private final EventStore eventStore;
	private final List<EventConverter> eventConverters = new ArrayList<>();
	private final EventSerializer eventSerializer;
	private final EventDispatcher eventDispatcher;


	public EventPublisherService(
			String publisherName,
			EventPublisherRepository eventPublisherRepository,
			EventStore eventStore,
			EventSerializer eventSerializer,
			EventDispatcher eventDispatcher
	) {
		this(publisherName, eventPublisherRepository, eventStore, new ArrayList<>(), eventSerializer, eventDispatcher);
	}

	public EventPublisherService(
			String publisherName,
			EventPublisherRepository eventPublisherRepository,
			EventStore eventStore,
			List<EventConverter> eventConverters,
			EventSerializer eventSerializer,
			EventDispatcher eventDispatcher

	) {
		assertArgumentNotNull(publisherName, "Publisher name may not be null");
		this.publisherName = publisherName;

		assertArgumentNotNull(eventPublisherRepository, "Publisher repository may not be null");
		this.eventPublisherRepository = eventPublisherRepository;

		assertArgumentNotNull(eventConverters, "Event converters list may not be null (empty is allowed");
		this.eventConverters.addAll(eventConverters);

		assertArgumentNotNull(eventStore, "Event store may not be null");
		this.eventStore = eventStore;

		assertArgumentNotNull(eventSerializer, "Event serializer may not be null");
		this.eventSerializer = eventSerializer;

		assertArgumentNotNull(eventDispatcher, "Event dispatcher may not be null");
		this.eventDispatcher = eventDispatcher;
	}


	@Transactional(rollbackFor = Exception.class)
	public void publishAllNonPublishedEvents() {

		EventPublisher publisher = eventPublisherRepository.publisherWithName(this.publisherName);
		if (publisher == null)
			throw new RuntimeException("Event publisher with name " + publisherName + " could not be found.");

		publisher.publishAllUnpublishedEventsFrom(eventStore, eventConverters, eventSerializer, eventDispatcher);

	}
}
