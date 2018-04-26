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

public class EventMessage {

	public static EventMessage from(StoredEvent storedEvent, EventSerializer eventSerializer) {
		Class<?> eventClass;
		try {
			eventClass = Class.forName(storedEvent.typeName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return new EventMessage(
				EventData.from(storedEvent), eventSerializer.unserialize(storedEvent.eventBody(), eventClass)

		);
	}

	private EventData eventData;

	private Object event;

	public EventMessage(EventData eventData, Object event) {
		this.event = event;
		this.eventData = eventData;
	}

	public EventData eventData() {
		return eventData;
	}

	public Object event() {
		return event;
	}

	public EventMessage withEvent(Object event) {
		return new EventMessage(eventData.withEventName(event.getClass().getName()), event);
	}
}
