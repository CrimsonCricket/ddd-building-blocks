
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

import com.crimsoncricket.ddd.port.adapter.hibernate.search.IdBridge;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.FieldBridge;

import javax.persistence.*;
import java.util.Objects;

import static com.crimsoncricket.asserts.Assert.assertArgumentNotNull;

@MappedSuperclass
public abstract class Entity<I extends Id> {

	@SuppressWarnings("JpaModelReferenceInspection")
	@EmbeddedId
	@DocumentId
	@FieldBridge(impl = IdBridge.class)
	@AttributeOverrides({
			@AttributeOverride(name = "id", column = @Column(name = "id"))
	})
	private I id;

	protected Entity() {
	}

	protected Entity(I id) {
		assertArgumentNotNull(id, "Id may not be null");
		this.id = id;
	}

	public I id() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Entity)) return false;
		Entity<?> entity = (Entity<?>) o;
		return Objects.equals(id, entity.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
