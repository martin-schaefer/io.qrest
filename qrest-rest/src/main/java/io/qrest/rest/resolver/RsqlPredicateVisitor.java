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

import static cz.jirutka.rsql.parser.ast.LogicalOperator.AND;
import static org.apache.commons.lang3.StringUtils.repeat;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class RsqlPredicateVisitor implements RSQLVisitor<Predicate, Void> {

	@NonNull
	private final PathResolver pathResolver;
	@NonNull
	private final BooleanExpressionFactory booleanExpressionFactory;
	private final BooleanBuilder builder = new BooleanBuilder();
	private int depth;

	@Override
	public Predicate visit(@NonNull AndNode node, Void param) {
		return visit(node);
	}

	@Override
	public Predicate visit(@NonNull OrNode node, Void param) {
		return visit(node);
	}

	@Override
	public Predicate visit(@NonNull ComparisonNode node, Void params) {
		return visit(node);
	}

	private Predicate visit(Node root) {
		walk(root, true);
		return builder;
	}

	private void walk(Node node, boolean thisAndOperation) {
		if (log.isDebugEnabled()) {
			depth++;
			log.debug(repeat('-', depth) + " " + node.getClass().getSimpleName() + ":" + node.toString());
		}
		if (node instanceof LogicalNode) {
			boolean lowerAndOperation = ((LogicalNode) node).getOperator().equals(AND);
			for (Node child : ((LogicalNode) node).getChildren()) {
				walk(child, lowerAndOperation);
			}
		} else if (node instanceof ComparisonNode) {
			BooleanExpression nextExpression = expression((ComparisonNode) node);
			if (thisAndOperation) {
				builder.and(nextExpression);
			} else {
				builder.or(nextExpression);
			}
		} else {
			throw new IllegalArgumentException("node is of unknown class: " + node.getClass().getName());
		}
		if (log.isDebugEnabled()) {
			depth--;
		}
	}

	private BooleanExpression expression(ComparisonNode node) {
		Path<?> path = pathResolver.getPath(node.getSelector());
		return booleanExpressionFactory.create(path, node.getOperator(), node.getArguments());
	}

}