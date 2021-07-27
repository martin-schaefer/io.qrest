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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Path;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@JsonSerialize(using = QueryEntitySerializer.class)
public class QueryEntity {

	@NonNull
	private final Tuple tuple;
	@NonNull
	private final Collection<Path<?>> propertiePaths;
	@NonNull
	private final String type;

	public List<Pair<String, Object>> getProperties() {
		List<Pair<String, Object>> properties = new ArrayList<>(propertiePaths.size());
		properties.add(new ImmutablePair<>("_type", type));
		for (Path<?> path : propertiePaths) {
			properties.add(new ImmutablePair<>(path.getMetadata().getName(), tuple.get(path)));
		}
		return properties;
	}
}
