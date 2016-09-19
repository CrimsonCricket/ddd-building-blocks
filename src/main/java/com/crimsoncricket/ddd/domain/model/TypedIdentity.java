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
 *
 */

package com.crimsoncricket.ddd.domain.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class TypedIdentity {

    @Column(nullable = false)
    private String identityType;

    @Column(nullable = false)
    private String identityValue;

    protected TypedIdentity() {}

    TypedIdentity(Class<? extends Id> identityClass, String identityValue) {
        this.identityType = identityClass.getName();
        this.identityValue = identityValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TypedIdentity)) return false;
        TypedIdentity that = (TypedIdentity) o;
        return Objects.equals(identityType, that.identityType) &&
                Objects.equals(identityValue, that.identityValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identityType, identityValue);
    }
}
