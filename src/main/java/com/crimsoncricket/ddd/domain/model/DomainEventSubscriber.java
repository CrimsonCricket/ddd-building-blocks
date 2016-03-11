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

package com.crimsoncricket.ddd.domain.model;

import static com.crimsoncricket.asserts.Assert.assertArgumentNotNull;

public abstract class DomainEventSubscriber<T extends  DomainEvent> {

    private Class<T> eventType;

    protected DomainEventSubscriber(Class<T> eventType) {
        this.eventType = eventType;
    }

    protected abstract void handleEvent(T aDomainEvent);

    @SuppressWarnings("unchecked")
    public void notify(DomainEvent anEvent) {
        assertArgumentNotNull(anEvent, "The domain event may not be null.");
        if (this.eventType.isAssignableFrom(anEvent.getClass()))
            this.handleEvent((T) anEvent);
    }


}
