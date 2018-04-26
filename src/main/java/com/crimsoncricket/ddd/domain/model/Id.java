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

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;

import static com.crimsoncricket.asserts.Assert.assertArgumentNotNull;
import static com.crimsoncricket.asserts.Assert.assertStringArgumentNotEmpty;

@MappedSuperclass
public abstract class Id implements Serializable {

	@Column(name = "id")
	private String id;

	// No-arg constructor to keep Hibernate happy
	protected Id() {
	}

	protected Id(String id) {
		assertArgumentNotNull(id, "id may not be null");
		assertStringArgumentNotEmpty(id, "id may not be empty");
		this.id = id;
	}

	@Override
	public String toString() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Id)) return false;
		Id id1 = (Id) o;
		return Objects.equals(id(), id1.id());
	}

	public String id() {
		return this.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id());
	}
}
