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

package com.crimsoncricket.ddd.port.adapter.hibernate.search;

import com.crimsoncricket.ddd.domain.model.Id;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.TwoWayFieldBridge;


public class IdBridge implements TwoWayFieldBridge {
    @Override
    public Object get(String name, Document document) {
        String stringValue = document.get(name);
        String className = document.get(classFieldName(name));
        Class idClass = null;
        try {
            idClass = Class.forName(className);
            //noinspection unchecked
            return idClass.getConstructor(String.class).newInstance(stringValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String objectToString(Object object) {
        return ((Id) object).id();
    }

    @Override
    public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
        String stringValue = objectToString(value);
        String className = value.getClass().getName();
        luceneOptions.addFieldToDocument(name, stringValue, document);
        luceneOptions.addFieldToDocument(classFieldName(name), className, document);
    }

    private String classFieldName(String name) {
        return "__" + name + "_class";
    }
}
