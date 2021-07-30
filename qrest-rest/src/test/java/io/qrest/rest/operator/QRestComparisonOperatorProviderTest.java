package io.qrest.rest.operator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;

public class QRestComparisonOperatorProviderTest {

	@Test
	public void getComparisonOperators() {
		// given
		QRestComparisonOperatorProvider qRestComparisonOperatorProvider = new QRestComparisonOperatorProvider();

		// when
		Set<ComparisonOperator> comparisonOperators = qRestComparisonOperatorProvider.getComparisonOperators();

		// then
		assertThat(comparisonOperators).isNotNull();
		assertThat(comparisonOperators).isNotEmpty();
	}

	@Test
	public void comparisonOperators_unmodifiable() {
		// given
		QRestComparisonOperatorProvider qRestComparisonOperatorProvider = new QRestComparisonOperatorProvider();
		Set<ComparisonOperator> comparisonOperators = qRestComparisonOperatorProvider.getComparisonOperators();

		// when
		Assertions.assertThrows(UnsupportedOperationException.class, () -> comparisonOperators.clear());

		// then: throw Exception
	}

}
