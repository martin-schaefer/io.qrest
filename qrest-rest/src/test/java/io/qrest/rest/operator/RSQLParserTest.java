package io.qrest.rest.operator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.Node;

public class RSQLParserTest {

	private static final String FIELD = "field";
	private static final String VALUE = "value";
	private static final ComparisonOperatorProvider comparisonOperatorProvider = new QRestComparisonOperatorProvider();

	@ParameterizedTest
	@MethodSource("provideExpressions")
	public void simpleExpressionShouldParse(String expression, String expectedSymbol) {
		// given: provided parameters

		// when
		Node node = new RSQLParser(comparisonOperatorProvider.getComparisonOperators()).parse(expression);
		assertThat(node).isNotNull();
		assertThat(node).isInstanceOf(ComparisonNode.class);
		assertThat(((ComparisonNode) node).getOperator().getSymbols()).contains(expectedSymbol);
	}

	private static Stream<Arguments> provideExpressions() {
		return comparisonOperatorProvider.getComparisonOperators().stream()
				.flatMap(o -> Arrays.stream(o.getSymbols()).map(s -> Arguments.of(FIELD + s + VALUE, s)));

	}
}
