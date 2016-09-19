/*
 * Copyright (c) Martijn van der Woud - The Crimson Cricket Internet Services - all rights reserved
 */

package com.crimsoncricket.ddd.port.adapter.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Year;

@Converter(autoApply = true)
public class YearConverter implements AttributeConverter<Year, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Year attribute) {
        if (attribute == null)
            return null;
        else
            return attribute.getValue();
    }

    @Override
    public Year convertToEntityAttribute(Integer dbData) {
        if (dbData == null)
            return null;
        else
            return Year.of(dbData);

    }
}
