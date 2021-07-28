package io.qrest.rest.operator;

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

	private static final String EQ = "==";
	private static final String NEQ = "!=";
	private static final String IN = "=in=";
	private static final String OUT = "=out=";
	private static final String NULL = "null";
	private final ConversionService conversionService;

	public BooleanExpression create(Path<?> path, String operatorSymbol, List<String> args) {
		// one value is guaranteed
		String arg = args.get(0);
		if (path instanceof SimpleExpression<?>) {
			SimpleExpression<Object> se = (SimpleExpression<Object>) path;
			switch (operatorSymbol) {
			case EQ:
				if (NULL.equals(arg)) {
					return se.isNull();
				} else {
					return se.eq(convert(arg, path));
				}
			case NEQ:
				if (NULL.equals(arg)) {
					return se.isNotNull();
				} else {
					return se.ne(convert(arg, path));
				}
			case IN:
				return se.in(convert(args, path));
			case OUT:
				return se.notIn(convert(args, path));
			}
		}
		throw new IllegalArgumentException("A property of type " + path.getClass().getSimpleName()
				+ " cannot be queried with operator " + operatorSymbol);
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
