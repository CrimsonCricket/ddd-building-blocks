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

import com.crimsoncricket.ddd.application.EventTracker;
import com.crimsoncricket.ddd.application.EventTrackerRepository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

public class EventTrackerRepositoryAdapter implements EventTrackerRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public EventTracker trackerWithName(String name) {
		return entityManager
				.createQuery("select t from EventTracker t where t.trackerName = :name", EventTracker.class)
				.setParameter("name", name)
				.setLockMode(LockModeType.PESSIMISTIC_WRITE)
				.getSingleResult();
	}

}
