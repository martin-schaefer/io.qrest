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
package io.qrest.rest.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.qrest.rest.conversion.ArgumentConversionService;
import io.qrest.rest.operator.BooleanExpressionFactory;
import io.qrest.rest.operator.ComparisonOperatorProvider;
import io.qrest.rest.resolver.QueryDslParamResolver;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Import(OperatorConfiguration.class)
@RequiredArgsConstructor
@Slf4j
public class QRestConfiguration implements WebMvcConfigurer {

	@NonNull
	private final ComparisonOperatorProvider comparisonOperatorProvider;

	@NonNull
	private final ArgumentConversionService argumentConversionService;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		BooleanExpressionFactory booleanExpressionFactory = new BooleanExpressionFactory(argumentConversionService);
		argumentResolvers.add(new QueryDslParamResolver(booleanExpressionFactory, comparisonOperatorProvider));
		log.info("Registered {} as argument resolver.", QueryDslParamResolver.class.getName());
	}

}
