/*
 * Copyright 2015 Martijn van der Woud - The Crimson Cricket Internet Services
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

package com.crimsoncricket.ddd.application;

import com.crimsoncricket.ddd.domain.model.DomainEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.ZoneId;

import static com.crimsoncricket.asserts.Assert.assertArgumentNotNull;

public class EventSerializerJSON implements EventSerializer {

    private Gson gson;

    public EventSerializerJSON() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(ZoneId.class, new TypeAdapter<ZoneId>() {
                    @Override
                    public void write(JsonWriter out, ZoneId value) throws IOException {
                        out.value(value == null ? null : value.getId());
                    }
                    @Override
                    public ZoneId read(JsonReader in) throws IOException {
                        if (in.peek() == JsonToken.NULL) {
                            in.nextNull();
                            return null;
                        } else {
                            return ZoneId.of(in.nextString());
                        }
                    }
                })
                .create();
    }

    @Override
    public String serialize(DomainEvent anEvent) {
        assertArgumentNotNull(anEvent, "The domain event may not be null.");
        return gson.toJson(anEvent);
    }

    @Override
    public <T extends DomainEvent> T unserialize(String serializedEvent, Class<T> eventClass) {
        return gson.fromJson(serializedEvent, eventClass);
    }


}
