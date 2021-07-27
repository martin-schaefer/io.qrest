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
package io.qrest.rest.resolver;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.SimpleExpression;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RsqlPredicateVisitor implements RSQLVisitor<Predicate, Void> {

	private boolean andOperator;
	private final BooleanBuilder booleanBuilder = new BooleanBuilder();
	private final PathResolver pathResolver;

	@Override
	public Predicate visit(AndNode node, Void param) {
		andOperator = true;
		return booleanBuilder;
	}

	@Override
	public Predicate visit(OrNode node, Void param) {
		andOperator = false;
		return booleanBuilder;
	}

	@Override
	public Predicate visit(ComparisonNode node, Void params) {
		Path<?> path = pathResolver.getPath(node.getSelector());
		Predicate predicate = null;
		if (path instanceof SimpleExpression<?>) {
			predicate = ((SimpleExpression<Object>) path).eq(node.getArguments().get(0));
		}
		if (predicate != null) {
			if (andOperator) {
				booleanBuilder.and(predicate);
			} else {
				booleanBuilder.or(predicate);
			}
		}
		return booleanBuilder;
	}

}