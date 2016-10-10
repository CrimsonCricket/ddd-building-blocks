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

public class OutdatedEntityVersionException extends Exception {

    private int expectedVersion;

    private int actualVersion;

    public OutdatedEntityVersionException(String message, int expectedVersion, int actualVersion) {
        super(message);
        this.expectedVersion = expectedVersion;
        this.actualVersion = actualVersion;
    }

    public int expectedVersion() {
        return expectedVersion;
    }

    public int actualVersion() {
        return actualVersion;
    }

}
