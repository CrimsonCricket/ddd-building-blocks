/*
 * Copyright 2015 Martijn van der Woud - The Crimson Cricket Internet Services
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


import com.crimsoncricket.ddd.domain.model.DomainEvent;
import com.crimsoncricket.ddd.domain.model.DomainEventPublisher;
import com.crimsoncricket.ddd.domain.model.DomainEventSubscriber;

import static com.crimsoncricket.asserts.Assert.assertArgumentNotNull;

public class CommandLifeCycle {

    private final PersistenceLifeCycle persistenceLifeCycle;
    private final EventStore eventStore;

    public CommandLifeCycle(PersistenceLifeCycle persistenceLifeCycle, EventStore eventStore) {
        assertArgumentNotNull(eventStore, "Event store may not be null");
        this.persistenceLifeCycle = persistenceLifeCycle;
        assertArgumentNotNull(persistenceLifeCycle, "Persistence life cycle may not be null");
        this.eventStore = eventStore;
    }

    public void start() {
        persistenceLifeCycle.begin();
        domainEventPublisher().reset();
        ensureThatAllPublishedEventsAreStored();
    }


    private void ensureThatAllPublishedEventsAreStored() {
        domainEventPublisher().subscribe(new DomainEventSubscriber<DomainEvent> (DomainEvent.class) {
            @Override
            protected void handleEvent(DomainEvent aDomainEvent) {
                eventStore.append(aDomainEvent);
            }
        });
    }

    private DomainEventPublisher domainEventPublisher() {
        return DomainEventPublisher.instance();
    }


    public void end() {
        persistenceLifeCycle.commit();
    }

    public void failure() {
        persistenceLifeCycle.rollback();
    }




}
