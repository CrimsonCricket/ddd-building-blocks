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
import java.util.stream.Stream;

@Entity
public class EventPublisher {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long publisherId;

	private String publisherName;

	private Long latestPublishedEventId;


	protected EventPublisher() {
	}

	public EventPublisher(String publisherName) {
		this.publisherName = publisherName;
		this.latestPublishedEventId = 0L;
	}

	void publishAllUnpublishedEventsFrom(
			EventStore eventStore,
			List<EventConverter> eventConverters,
			EventSerializer serializer,
			EventDispatcher dispatcher
	) {
		Stream<SequencedEvent> eventStream = eventStore
				.allEventsAfter(latestPublishedEventId)
				.stream()
				.map(storedEvent -> storedEvent.toSequencedEvent(serializer));

		for (EventConverter converter : eventConverters)
			eventStream = eventStream.map(converter::converted);

		List<SequencedEvent> eventList = eventStream.collect(Collectors.toList());
		eventList.forEach(dispatcher::dispatch);

		if (! eventList.isEmpty())
			this.latestPublishedEventId = eventList.get(eventList.size() - 1).eventId();
	}


	public String publisherName() {
		return publisherName;
	}

	public Long latestPublishedEventId() {
		return latestPublishedEventId;
	}
}
