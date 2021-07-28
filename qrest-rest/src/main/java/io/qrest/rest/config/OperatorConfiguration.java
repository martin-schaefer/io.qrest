package io.qrest.rest.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.qrest.rest.operator.ComparisonOperatorProvider;
import io.qrest.rest.operator.QRestComparisonOperatorProvider;

@Configuration
public class OperatorConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ComparisonOperatorProvider ComparisonOperatorProvider() {
		return new QRestComparisonOperatorProvider();
	}
}
