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

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public class AggregateRoot<I extends Id> extends Entity<I> {

	@Version
	private Integer version;

	protected AggregateRoot() {
	}

	public AggregateRoot(I id) {
		super(id);
	}

	public Integer version() {
		return version;
	}

	protected void ensureVersionIs(int expectedVersion) throws OutdatedEntityVersionException {
		if (version != expectedVersion)
			throw new OutdatedEntityVersionException(
					"Attempted to execute a command on an outdated entity of type " + this.getClass().getSimpleName(),
					expectedVersion,
					version);

	}

	// This method exists for testing purposes only
	protected void assumeVersion(int version) {
		this.version = version;
	}

}
