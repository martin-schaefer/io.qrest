package io.qrest.rest.resolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.core.convert.ConversionService;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BooleanExpressionFactory {

	private final ConversionService conversionService;

	public BooleanExpression create(Path<?> path, ComparisonOperator op, List<String> args) {
		String arg = null;
		if (!op.isMultiValue()) {
			arg = args.get(0);
		}
		if (path instanceof SimpleExpression<?>) {
			if (isOp(op, "==")) {
				return ((SimpleExpression<Object>) path).eq(convert(arg, path));
			}
			if (isOp(op, "!=")) {
				return ((SimpleExpression<Object>) path).ne(convert(arg, path));
			}
			if (isOp(op, "=in=")) {
				return ((SimpleExpression<Object>) path).in(convert(args, path));
			}
			if (isOp(op, "=out=")) {
				return ((SimpleExpression<Object>) path).notIn(convert(args, path));
			}
		}
		throw new IllegalArgumentException(
				"Path of class " + path.getClass().getSimpleName() + " cannot be used with operator " + op.getSymbol());
	}

	private Object convert(String source, Path<?> path) {
		if (path.getType().equals(String.class)) {
			// no conversion required
			return source;
		}
		return conversionService.convert(conversionService, path.getType());
	}

	private Collection<?> convert(Collection<String> sourceCollection, Path<?> path) {
		if (path.getType().equals(String.class)) {
			// no conversion required
			return sourceCollection;
		}
		Collection<Object> convertedList = new ArrayList<>(sourceCollection.size());
		for (String source : sourceCollection) {
			convertedList.add(convert(source, path));
		}
		return convertedList;
	}

	private boolean isOp(ComparisonOperator op, String primarySymbol) {
		return (op.getSymbol().equals(primarySymbol));
	}
}
