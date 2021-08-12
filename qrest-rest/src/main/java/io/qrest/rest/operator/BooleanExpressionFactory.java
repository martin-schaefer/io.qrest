package io.qrest.rest.operator;

import static io.qrest.rest.operator.OperatorSymbols.EQUAL;
import static io.qrest.rest.operator.OperatorSymbols.GREATER_THAN;
import static io.qrest.rest.operator.OperatorSymbols.GREATER_THAN_OR_EQUAL;
import static io.qrest.rest.operator.OperatorSymbols.IN;
import static io.qrest.rest.operator.OperatorSymbols.LESS_THAN;
import static io.qrest.rest.operator.OperatorSymbols.LESS_THAN_OR_EQUAL;
import static io.qrest.rest.operator.OperatorSymbols.LIKE;
import static io.qrest.rest.operator.OperatorSymbols.LIKE_CASE_INSENSITIVE;
import static io.qrest.rest.operator.OperatorSymbols.NOT_EQUAL;
import static io.qrest.rest.operator.OperatorSymbols.NOT_IN;
import static io.qrest.rest.operator.OperatorSymbols.NULL_VALUE;
import static org.apache.commons.lang3.StringUtils.replace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.TemporalExpression;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BooleanExpressionFactory {

	@NonNull
	private final ArgumentConversionService argumentConversionService;

	public BooleanExpression create(Path<?> path, String operatorSymbol, List<String> args) {
		// one value is guaranteed
		String arg = args.get(0);
		if (path instanceof StringExpression) {
			StringExpression se = (StringExpression) path;
			switch (operatorSymbol) {
			case LIKE:
				return se.like(replace(arg, "*", "%"));
			case LIKE_CASE_INSENSITIVE:
				return se.likeIgnoreCase(replace(arg, "*", "%"));
			}
		} else if (path instanceof NumberExpression) {
			NumberExpression ne = (NumberExpression) path;
			switch (operatorSymbol) {
			case GREATER_THAN:
				return ne.gt((Number) convert(arg, path));
			case GREATER_THAN_OR_EQUAL:
				return ne.goe((Number) convert(arg, path));
			case LESS_THAN:
				return ne.lt((Number) convert(arg, path));
			case LESS_THAN_OR_EQUAL:
				return ne.loe((Number) convert(arg, path));
			}
		} else if (path instanceof TemporalExpression) {
			TemporalExpression te = (TemporalExpression) path;
			switch (operatorSymbol) {
			case GREATER_THAN:
				return te.gt((Comparable) convert(arg, path));
			case GREATER_THAN_OR_EQUAL:
				return te.goe((Comparable) convert(arg, path));
			case LESS_THAN:
				return te.lt((Comparable) convert(arg, path));
			case LESS_THAN_OR_EQUAL:
				return te.loe((Comparable) convert(arg, path));
			}
		}
		if (path instanceof SimpleExpression<?>) {
			SimpleExpression<Object> se = (SimpleExpression<Object>) path;
			switch (operatorSymbol) {
			case EQUAL:
				if (NULL_VALUE.equals(arg)) {
					return se.isNull();
				} else {
					return se.eq(convert(arg, path));
				}
			case NOT_EQUAL:
				if (NULL_VALUE.equals(arg)) {
					return se.isNotNull();
				} else {
					return se.ne(convert(arg, path));
				}
			case IN:
				return se.in(convert(args, path));
			case NOT_IN:
				return se.notIn(convert(args, path));
			}
		}
		throw new QRestOperatorException("A path of type " + path.getClass().getName()
				+ " cannot be used with operator " + operatorSymbol);
	}

	private Object convert(String source, Path<?> path) {
		if (path.getType().equals(String.class)) {
			// no conversion required
			return source;
		}
		return argumentConversionService.convert(source, path.getType());
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

}
