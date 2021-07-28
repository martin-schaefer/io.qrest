package io.qrest.rest.operator;

import java.util.Optional;
import java.util.Set;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;

public interface ComparisonOperatorProvider {

	Set<ComparisonOperator> getComparisonOperators();

	Optional<ComparisonOperator> getComparisonOperatorBySymbol(String symbol);
}
