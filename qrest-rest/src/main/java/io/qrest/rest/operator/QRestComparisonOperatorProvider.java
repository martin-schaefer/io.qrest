package io.qrest.rest.operator;

import static io.qrest.rest.operator.OperatorSymbols.EQUAL;
import static io.qrest.rest.operator.OperatorSymbols.GREATER_THAN;
import static io.qrest.rest.operator.OperatorSymbols.GREATER_THAN_ALT;
import static io.qrest.rest.operator.OperatorSymbols.GREATER_THAN_OR_EQUAL;
import static io.qrest.rest.operator.OperatorSymbols.GREATER_THAN_OR_EQUAL_ALT;
import static io.qrest.rest.operator.OperatorSymbols.IN;
import static io.qrest.rest.operator.OperatorSymbols.LESS_THAN;
import static io.qrest.rest.operator.OperatorSymbols.LESS_THAN_ALT;
import static io.qrest.rest.operator.OperatorSymbols.LESS_THAN_OR_EQUAL;
import static io.qrest.rest.operator.OperatorSymbols.LESS_THAN_OR_EQUAL_ALT;
import static io.qrest.rest.operator.OperatorSymbols.LIKE;
import static io.qrest.rest.operator.OperatorSymbols.LIKE_CASE_INSENSITIVE;
import static io.qrest.rest.operator.OperatorSymbols.NOT_EQUAL;
import static io.qrest.rest.operator.OperatorSymbols.NOT_IN;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import lombok.NonNull;

public class QRestComparisonOperatorProvider implements ComparisonOperatorProvider {

	private final Map<String, ComparisonOperator> comparisonOperatorsMap;
	private final Set<ComparisonOperator> comparisonOperatorsSet;

	public QRestComparisonOperatorProvider() {
		Map<String, ComparisonOperator> c = new HashMap<>(10);
		add(c, new ComparisonOperator(EQUAL));
		add(c, new ComparisonOperator(NOT_EQUAL));
		add(c, new ComparisonOperator(IN, true));
		add(c, new ComparisonOperator(NOT_IN, true));
		add(c, new ComparisonOperator(GREATER_THAN, GREATER_THAN_ALT));
		add(c, new ComparisonOperator(GREATER_THAN_OR_EQUAL, GREATER_THAN_OR_EQUAL_ALT));
		add(c, new ComparisonOperator(LESS_THAN, LESS_THAN_ALT));
		add(c, new ComparisonOperator(LESS_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL_ALT));
		add(c, new ComparisonOperator(LIKE));
		add(c, new ComparisonOperator(LIKE_CASE_INSENSITIVE));
		this.comparisonOperatorsMap = unmodifiableMap(c);
		this.comparisonOperatorsSet = unmodifiableSet(new HashSet<>(c.values()));
	}

	private void add(Map<String, ComparisonOperator> map, ComparisonOperator o) {
		map.put(o.getSymbol(), o);
	}

	@Override
	public Set<ComparisonOperator> getComparisonOperators() {
		return comparisonOperatorsSet;
	}

	@Override
	public Optional<ComparisonOperator> getComparisonOperatorBySymbol(@NonNull String symbol) {
		return Optional.ofNullable(comparisonOperatorsMap.get(symbol));
	}

}
