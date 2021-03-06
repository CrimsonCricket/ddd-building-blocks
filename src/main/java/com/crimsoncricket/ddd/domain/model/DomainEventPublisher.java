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

package com.crimsoncricket.ddd.domain.model;

import java.util.ArrayList;
import java.util.List;

import static com.crimsoncricket.asserts.Assert.assertArgumentNotNull;

public class DomainEventPublisher {

	private static final ThreadLocal<DomainEventPublisher> instance =
			ThreadLocal.withInitial(DomainEventPublisher::new);

	public static DomainEventPublisher instance() {
		return instance.get();
	}

	private List<DomainEventSubscriber> subscribers;

	public DomainEventPublisher() {
		newSubscribersList();
	}

	private void newSubscribersList() {
		this.subscribers = new ArrayList<>();
	}

	public void publish(final DomainEvent anEvent) {
		assertArgumentNotNull(anEvent, "The domain event may not be null.");
		this.subscribers.forEach(subscriber -> subscriber.notify(anEvent));
	}

	public void subscribe(DomainEventSubscriber aSubscriber) {
		assertArgumentNotNull(aSubscriber, "The domain event subscriber may not be null");
		this.subscribers.add(aSubscriber);
	}

	public void reset() {
		newSubscribersList();
	}
}
