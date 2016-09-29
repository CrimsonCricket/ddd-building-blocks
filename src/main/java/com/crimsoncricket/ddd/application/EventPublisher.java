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
 *
 */

package com.crimsoncricket.ddd.application;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class EventPublisher {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long publisherId;

    private String publisherName;

    private Long latestPublishedEventId;


    protected EventPublisher(){}

    public EventPublisher(String publisherName) {
        this.publisherName = publisherName;
        this.latestPublishedEventId = 0L;
    }

    void publishAllUnpublishedEventsFrom(
            EventStore eventStore,
            EventSerializer serializer,
            EventDispatcher dispatcher
    ) {
        List<StoredEvent> storedEvents = eventStore.allEventsAfter(latestPublishedEventId);
        List<SequencedEvent> sequencedEvents = storedEvents.stream()
                .map(storedEvent -> storedEvent.toSequencedEvent(serializer))
                .collect(Collectors.toList());

        sequencedEvents
                .forEach(dispatcher::dispatch);

        SequencedEvent lastEvent = sequencedEvents.stream().reduce((a,b) ->b).orElse(null);
        if (lastEvent != null)
            this.latestPublishedEventId = lastEvent.eventId();

    }


    public String publisherName() {
        return publisherName;
    }

    public Long latestPublishedEventId() {
        return latestPublishedEventId;
    }
}
