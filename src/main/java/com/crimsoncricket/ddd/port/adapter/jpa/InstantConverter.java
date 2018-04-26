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

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.Instant;

@Converter(autoApply = true)
public class InstantConverter implements AttributeConverter<Instant, Timestamp> {

	@Override
	public Timestamp convertToDatabaseColumn(Instant attribute) {
		if (attribute == null)
			return null;

		return Timestamp.from(attribute);
	}

	@Override
	public Instant convertToEntityAttribute(Timestamp dbData) {
		if (dbData == null)
			return null;

		return dbData.toInstant();
	}
}
