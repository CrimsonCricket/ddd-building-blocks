/*
 * Copyright 2018 Martijn van der Woud - The Crimson Cricket Internet Services
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

import org.springframework.transaction.annotation.Transactional;

public class TrackingEventProcessor {

	private final String trackerName;

	private final EventProcessor eventProcessor;

	public TrackingEventProcessor(String trackerName, EventProcessor eventProcessor) {
		this.trackerName = trackerName;
		this.eventProcessor = eventProcessor;
	}

	/**
	 * Processes events from the provided event store which have not been processed earlier
	 *
	 * @param eventTrackerRepository The repository used to retrieve the configured tracker by name
	 * @param eventStore             The store to be used by the even tracker to retrieve events
	 * @param eventSerializer        The serializer to use for restoring events from their serialized representation
	 *                               in the store
	 * @return true, if all events have been processed by the tracker; false, if there are more events left to process
	 * after executing this method
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean processEvents(
			EventTrackerRepository eventTrackerRepository, EventStore eventStore, EventSerializer eventSerializer
	) {
		EventTracker tracker = eventTrackerRepository.trackerWithName(trackerName);
		return tracker.processEvents(eventStore, eventSerializer, eventProcessor);
	}

}
