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

package com.crimsoncricket.ddd.port.adapter.jpa;

import com.crimsoncricket.ddd.application.EventInitiatorResolver;
import com.crimsoncricket.ddd.application.EventSerializer;
import com.crimsoncricket.ddd.application.AbstractEventStore;
import com.crimsoncricket.ddd.application.StoredEvent;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.ArrayList;
import java.util.List;

public class JpaEventStore extends AbstractEventStore {

    @PersistenceContext
    private EntityManager entityManager;


    public JpaEventStore(
            EventSerializer eventSerializer,
            EventInitiatorResolver initiatorResolver) {
        super(eventSerializer, initiatorResolver);
    }

    @Override
    protected void store(StoredEvent storedEvent) {
        entityManager.persist(storedEvent);
    }

    @Override
    public List<StoredEvent> allEventsAfter(Long eventId) {
        Query query = entityManager
                .createQuery("select e from StoredEvent e where e.eventId > :eventId")
                .setParameter("eventId", eventId);

        List<StoredEvent> eventList = new ArrayList<>();
        List resultList = query.getResultList();
        for (Object result : resultList) {
            if (result instanceof StoredEvent)
                eventList.add((StoredEvent) result);
            else
                throw new RuntimeException("Unexpected result type."); // not gonna happen
        }
        return eventList;
    }





}
