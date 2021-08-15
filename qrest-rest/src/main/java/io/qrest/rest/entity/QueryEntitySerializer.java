/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.qrest.rest.entity;

import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class QueryEntitySerializer extends JsonSerializer<QueryEntity> {

	@Override
	public void serialize(QueryEntity queryItem, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject();
		for (Pair<String, Object> property : queryItem.getProperties()) {
			gen.writeFieldName(property.getKey());
			Object value = property.getValue();
			if (value != null) {
				provider.findValueSerializer(value.getClass()).serialize(value, gen, provider);
			} else {
				provider.getDefaultNullValueSerializer().serialize(value, gen, provider);
			}
		}
		gen.writeEndObject();
	}

}
