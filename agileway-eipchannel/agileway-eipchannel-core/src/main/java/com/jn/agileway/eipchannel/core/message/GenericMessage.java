/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.agileway.eipchannel.core.message;

import com.jn.langx.util.collection.Collects;

import java.util.Map;

/**
 * Base Message class defining common properties such as id, payload, and headers.
 * Once created this object is immutable.
 */
public class GenericMessage<T> extends BaseMessage<Object, T> {

    private static final long serialVersionUID = 1L;

    public GenericMessage() {

    }

    /**
     * Create a new message with the given payload.
     *
     * @param payload the message payload
     */
    public GenericMessage(T payload) {
        this(payload, null);
    }

    /**
     * Create a new message with the given payload. The provided map
     * will be used to populate the message headers
     *
     * @param payload the message payload
     * @param headers message headers
     * @see MessageHeaders
     */
    public GenericMessage(T payload, Map<String, Object> headers) {
        setHeaders(headers);
        setPayload(payload);
    }
    public void setPayload(T payload) {
        this.payload = payload;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = new MessageHeaders(Collects.newHashMap(headers));
    }
    public String toString() {
        return "[Payload=" + this.payload + "][Headers=" + this.headers + "]";
    }

}
