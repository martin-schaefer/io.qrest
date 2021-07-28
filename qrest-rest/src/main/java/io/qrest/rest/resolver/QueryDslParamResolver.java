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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import io.qrest.rest.operator.BooleanExpressionFactory;
import io.qrest.rest.operator.ComparisonOperatorProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QueryDslParamResolver implements HandlerMethodArgumentResolver {

	@NonNull
	private final BooleanExpressionFactory booleanExpressionFactory;

	@NonNull
	private final ComparisonOperatorProvider comparisonOperatorProvider;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(QueryDslParam.class)
				&& parameter.getParameterAnnotation(QueryDslParamSpec.class) != null;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		Class<? extends PathResolver> relationalPathProviderClass = parameter
				.getParameterAnnotation(QueryDslParamSpec.class).value();
		PathResolver pathResolver = relationalPathProviderClass.getDeclaredConstructor().newInstance();
		HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
		return new QueryDslParam(extractSelect(request, pathResolver), extractWhere(request, pathResolver),
				Optional.empty());
	}

	private Optional<List<Path<?>>> extractSelect(HttpServletRequest request, PathResolver pathResolver) {
		String select = request.getParameter("select");
		if (StringUtils.isBlank(select)) {
			return Optional.empty();
		}
		String[] names = StringUtils.split(select, ',');
		final List<Path<?>> pathes = new ArrayList<>(names.length);
		for (String name : names) {
			pathes.add(pathResolver.getPath(name));
		}
		return Optional.of(pathes);
	}

	private Optional<Predicate> extractWhere(HttpServletRequest request, PathResolver pathResolver) {
		String where = request.getParameter("where");
		if (StringUtils.isBlank(where)) {
			return Optional.empty();
		}
		Set<ComparisonOperator> operators = RSQLOperators.defaultOperators();
		operators.add(new ComparisonOperator("=lk=", false));
		Node rootNode = new RSQLParser(comparisonOperatorProvider.getComparisonOperators()).parse(where);
		Predicate predicate = rootNode.accept(new RsqlPredicateVisitor(pathResolver, booleanExpressionFactory));
		return Optional.of(predicate);
	}
}
