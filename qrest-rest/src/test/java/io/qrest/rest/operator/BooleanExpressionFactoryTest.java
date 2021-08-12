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
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

@ExtendWith(MockitoExtension.class)
public class BooleanExpressionFactoryTest {

	private static final BooleanExpression expectedExpression = mock(BooleanExpression.class);

	@ParameterizedTest
	@MethodSource("simpleExpressions_withStrings_source")
	public void simpleExpressions_withStrings(String operatorSymbol,
			Function<String, StringPath> mockFunction,
			BiConsumer<StringPath, String> verifyFunction) {

		// given
		ConversionService conversionService = mock(ConversionService.class);
		String value = "abc";
		StringPath path = mockFunction.apply(value);
		BooleanExpressionFactory booleanExpressionFactory = new BooleanExpressionFactory(conversionService);

		// when
		BooleanExpression createdExpression = booleanExpressionFactory.create(path, operatorSymbol,
				List.of(value));

		// then
		assertThat(createdExpression).isNotNull();
		verifyFunction.accept(path, value);
		then(path).shouldHaveNoMoreInteractions();
		then(conversionService).shouldHaveNoInteractions();
	}

	private static Stream<Arguments> simpleExpressions_withStrings_source() {
		return Stream.of(Arguments.of(EQUAL, (Function<String, StringPath>) (p) -> {
			StringPath mock = stringPath(true);
			given(mock.eq(p)).willReturn(expectedExpression);
			return mock;
		}, (BiConsumer<StringPath, String>) (m, p) -> {
			then(m).should().getType();
			then(m).should().eq(p);
		}), Arguments.of(NOT_EQUAL, (Function<String, StringPath>) (p) -> {
			StringPath mock = stringPath(true);
			given(mock.ne(p)).willReturn(expectedExpression);
			return mock;
		}, (BiConsumer<StringPath, String>) (m, p) -> {
			then(m).should().getType();
			then(m).should().ne(p);
		}), Arguments.of(IN, (Function<String, StringPath>) (p) -> {
			StringPath mock = stringPath(true);
			given(mock.in(List.of(p))).willReturn(expectedExpression);
			return mock;
		}, (BiConsumer<StringPath, String>) (m, p) -> {
			then(m).should().getType();
			then(m).should().in(List.of(p));
		}), Arguments.of(NOT_IN, (Function<String, StringPath>) (p) -> {
			StringPath mock = stringPath(true);
			given(mock.notIn(List.of(p))).willReturn(expectedExpression);
			return mock;
		}, (BiConsumer<StringPath, String>) (m, p) -> {
			then(m).should().getType();
			then(m).should().notIn(List.of(p));
		}), Arguments.of(LIKE, (Function<String, StringPath>) (p) -> {
			StringPath mock = stringPath(false);
			given(mock.like(p)).willReturn(expectedExpression);
			return mock;
		}, (BiConsumer<StringPath, String>) (m, p) -> {
			then(m).should().like(p);
		}), Arguments.of(LIKE_CASE_INSENSITIVE, (Function<String, StringPath>) (p) -> {
			StringPath mock = stringPath(false);
			given(mock.likeIgnoreCase(p)).willReturn(expectedExpression);
			return mock;
		}, (BiConsumer<StringPath, String>) (m, p) -> {
			then(m).should().likeIgnoreCase(p);
		}));
	}

	@ParameterizedTest
	@MethodSource("simpleExpressions_withBooleans_source")
	public void simpleExpressions_withBooleans(String operatorSymbol,
			Function<Boolean, BooleanPath> mockFunction,
			BiConsumer<BooleanPath, Boolean> verifyFunction) {

		// given
		Boolean value = Boolean.TRUE;
		ConversionService conversionService = mock(ConversionService.class);
		given(conversionService.convert(value.toString(), Boolean.class)).willReturn(value);
		BooleanPath path = mockFunction.apply(value);
		BooleanExpressionFactory booleanExpressionFactory = new BooleanExpressionFactory(conversionService);

		// when
		BooleanExpression createdExpression = booleanExpressionFactory.create(path, operatorSymbol,
				List.of(value.toString()));

		// then
		assertThat(createdExpression).isNotNull();
		verifyFunction.accept(path, value);
		then(path).shouldHaveNoMoreInteractions();
		then(conversionService).should().convert(value.toString(), Boolean.class);
	}

	private static Stream<Arguments> simpleExpressions_withBooleans_source() {
		return Stream.of(Arguments.of(EQUAL, (Function<Boolean, BooleanPath>) (p) -> {
			BooleanPath mock = booleanPath(true);
			given(mock.eq(p)).willReturn(expectedExpression);
			return mock;
		}, (BiConsumer<BooleanPath, Boolean>) (m, p) -> {
			then(m).should(times(2)).getType();
			then(m).should().eq(p);
		}), Arguments.of(NOT_EQUAL, (Function<Boolean, BooleanPath>) (p) -> {
			BooleanPath mock = booleanPath(true);
			given(mock.ne(p)).willReturn(expectedExpression);
			return mock;
		}, (BiConsumer<BooleanPath, Boolean>) (m, p) -> {
			then(m).should(times(2)).getType();
			then(m).should().ne(p);
		}));
	}

	@ParameterizedTest
	@MethodSource("numberExpressions_withIntegers_source")
	public void numberExpressions_withIntegers(String operatorSymbol,
			Function<Integer, NumberPath<?>> mockFunction,
			BiConsumer<NumberPath<?>, Integer> verifyFunction) {

		// given
		Integer value = Integer.valueOf(5);
		NumberPath<?> path = mockFunction.apply(value);
		ConversionService conversionService = mock(ConversionService.class);
		given(conversionService.convert(value.toString(), Integer.class)).willReturn(value);
		BooleanExpressionFactory booleanExpressionFactory = new BooleanExpressionFactory(conversionService);

		// when
		BooleanExpression createdExpression = booleanExpressionFactory.create(path, operatorSymbol,
				List.of(value.toString()));

		// then
		assertThat(createdExpression).isNotNull();
		verifyFunction.accept(path, value);
		then(path).shouldHaveNoMoreInteractions();
		then(conversionService).should().convert(value.toString(), Integer.class);
	}

	private static Stream<Arguments> numberExpressions_withIntegers_source() {
		return Stream.of(Arguments.of(GREATER_THAN, (Function<Integer, NumberPath<?>>) (p) -> {
			NumberPath<?> mock = numberPath(Integer.class);
			given(mock.gt(p)).willReturn(expectedExpression);
			return mock;
		}, (BiConsumer<NumberPath<?>, Integer>) (m, p) -> {
			then(m).should(times(2)).getType();
			then(m).should().gt(p);
		}),
				Arguments.of(GREATER_THAN_OR_EQUAL, (Function<Integer, NumberPath<?>>) (p) -> {
					NumberPath<?> mock = numberPath(Integer.class);
					given(mock.goe(p)).willReturn(expectedExpression);
					return mock;
				}, (BiConsumer<NumberPath<?>, Integer>) (m, p) -> {
					then(m).should(times(2)).getType();
					then(m).should().goe(p);
				}),
				Arguments.of(LESS_THAN, (Function<Integer, NumberPath<?>>) (p) -> {
					NumberPath<?> mock = numberPath(Integer.class);
					given(mock.lt(p)).willReturn(expectedExpression);
					return mock;
				}, (BiConsumer<NumberPath<?>, Integer>) (m, p) -> {
					then(m).should(times(2)).getType();
					then(m).should().lt(p);
				}),
				Arguments.of(LESS_THAN_OR_EQUAL, (Function<Integer, NumberPath<?>>) (p) -> {
					NumberPath<?> mock = numberPath(Integer.class);
					given(mock.loe(p)).willReturn(expectedExpression);
					return mock;
				}, (BiConsumer<NumberPath<?>, Integer>) (m, p) -> {
					then(m).should(times(2)).getType();
					then(m).should().loe(p);
				}));
	}

	@ParameterizedTest
	@MethodSource("temporalExpressions_withLocalDates_source")
	public void temporalExpressions_withLocalDates(String operatorSymbol,
			Function<LocalDate, DatePath<?>> mockFunction,
			BiConsumer<DatePath<?>, LocalDate> verifyFunction) {

		// given
		LocalDate value = LocalDate.of(2022, 9, 28);
		DatePath<?> path = mockFunction.apply(value);
		ConversionService conversionService = mock(ConversionService.class);
		given(conversionService.convert(value.toString(), LocalDate.class)).willReturn(value);
		BooleanExpressionFactory booleanExpressionFactory = new BooleanExpressionFactory(conversionService);

		// when
		BooleanExpression createdExpression = booleanExpressionFactory.create(path, operatorSymbol,
				List.of(value.toString()));

		// then
		assertThat(createdExpression).isNotNull();
		verifyFunction.accept(path, value);
		then(path).shouldHaveNoMoreInteractions();
		then(conversionService).should().convert(value.toString(), LocalDate.class);
	}

	private static Stream<Arguments> temporalExpressions_withLocalDates_source() {
		return Stream.of(Arguments.of(GREATER_THAN, (Function<LocalDate, DatePath<?>>) (p) -> {
			DatePath<LocalDate> mock = datePath(LocalDate.class);
			given(mock.gt(p)).willReturn(expectedExpression);
			return mock;
		}, (BiConsumer<DatePath<LocalDate>, LocalDate>) (m, p) -> {
			then(m).should(times(2)).getType();
			then(m).should().gt(p);
		}),
				Arguments.of(GREATER_THAN_OR_EQUAL, (Function<LocalDate, DatePath<?>>) (p) -> {
					DatePath<LocalDate> mock = datePath(LocalDate.class);
					given(mock.goe(p)).willReturn(expectedExpression);
					return mock;
				}, (BiConsumer<DatePath<LocalDate>, LocalDate>) (m, p) -> {
					then(m).should(times(2)).getType();
					then(m).should().goe(p);
				}),
				Arguments.of(LESS_THAN, (Function<LocalDate, DatePath<?>>) (p) -> {
					DatePath<LocalDate> mock = datePath(LocalDate.class);
					given(mock.lt(p)).willReturn(expectedExpression);
					return mock;
				}, (BiConsumer<DatePath<LocalDate>, LocalDate>) (m, p) -> {
					then(m).should(times(2)).getType();
					then(m).should().lt(p);
				}),
				Arguments.of(LESS_THAN_OR_EQUAL, (Function<LocalDate, DatePath<?>>) (p) -> {
					DatePath<LocalDate> mock = datePath(LocalDate.class);
					given(mock.loe(p)).willReturn(expectedExpression);
					return mock;
				}, (BiConsumer<DatePath<LocalDate>, LocalDate>) (m, p) -> {
					then(m).should(times(2)).getType();
					then(m).should().loe(p);
				}));
	}

	@ParameterizedTest
	@MethodSource("collectionExpressions_withBigDecimals_source")
	public void collectionExpressions_withBigDecimals(String operatorSymbol,
			Function<List<BigDecimal>, NumberPath<BigDecimal>> mockFunction,
			BiConsumer<NumberPath<BigDecimal>, List<BigDecimal>> verifyFunction) {

		// given
		List<BigDecimal> values = List.of(new BigDecimal("3.5"), new BigDecimal("33.23"), new BigDecimal("320"));
		NumberPath<BigDecimal> path = mockFunction.apply(values);
		ConversionService conversionService = mock(ConversionService.class);
		for (BigDecimal value : values) {
			given(conversionService.convert(value.toString(), BigDecimal.class)).willReturn(value);
		}
		BooleanExpressionFactory booleanExpressionFactory = new BooleanExpressionFactory(conversionService);

		// when
		BooleanExpression createdExpression = booleanExpressionFactory.create(path, operatorSymbol, values.stream().map(v -> v.toString()).collect(toList()));

		// then
		assertThat(createdExpression).isNotNull();
		verifyFunction.accept(path, values);
		then(path).shouldHaveNoMoreInteractions();
		for (BigDecimal value : values) {
			then(conversionService).should().convert(value.toString(), BigDecimal.class);
		}
	}

	private static Stream<Arguments> collectionExpressions_withBigDecimals_source() {
		return Stream.of(Arguments.of(IN, (Function<List<BigDecimal>, NumberPath<?>>) (p) -> {
			NumberPath<BigDecimal> mock = numberPath(BigDecimal.class);
			given(mock.in(p)).willReturn(expectedExpression);
			return mock;
		}, (BiConsumer<NumberPath<BigDecimal>, List<BigDecimal>>) (m, p) -> {
			then(m).should(times(2 * p.size() + 1)).getType();
			then(m).should().in((p));
		}),
				Arguments.of(NOT_IN, (Function<List<BigDecimal>, NumberPath<?>>) (p) -> {
					NumberPath<BigDecimal> mock = numberPath(BigDecimal.class);
					given(mock.notIn(p)).willReturn(expectedExpression);
					return mock;
				}, (BiConsumer<NumberPath<BigDecimal>, List<BigDecimal>>) (m, p) -> {
					then(m).should(times(2 * p.size() + 1)).getType();
					then(m).should().notIn((p));
				}));
	}

	@ParameterizedTest
	@MethodSource("mixedExpressions_withInvalidOperators")
	public void mixedExpressions_withInvalidOperators_shouldThrowQRestOperatorException(String operatorSymbol, Supplier<Path<?>> pathSupplier) {
		ConversionService conversionService = mock(ConversionService.class);
		BooleanExpressionFactory booleanExpressionFactory = new BooleanExpressionFactory(conversionService);
		assertThrows(QRestOperatorException.class, () -> booleanExpressionFactory.create(pathSupplier.get(), operatorSymbol, List.of("not_used")));
	}

	private static Stream<Arguments> mixedExpressions_withInvalidOperators() {
		return Stream.of(Arguments.of(GREATER_THAN, (Supplier<Path<?>>) () -> {
			return stringPath(false);
		}),
				Arguments.of(LESS_THAN, (Supplier<Path<?>>) () -> {
					return stringPath(false);
				}),
				Arguments.of(GREATER_THAN_OR_EQUAL, (Supplier<Path<?>>) () -> {
					return booleanPath(false);
				}),
				Arguments.of(LESS_THAN_OR_EQUAL, (Supplier<Path<?>>) () -> {
					return booleanPath(false);
				}),
				Arguments.of(LIKE, (Supplier<Path<?>>) () -> {
					return booleanPath(false);
				}),
				Arguments.of(LIKE, (Supplier<Path<?>>) () -> {
					return numberPath(null);
				}));
	}

	@Test
	public void constructorWithNull_shouldThrowNullPointerException() {
		Assertions.assertThrows(NullPointerException.class, () -> new BooleanExpressionFactory(null));
	}

	private static StringPath stringPath(boolean withType) {
		StringPath stringPath = mock(StringPath.class);
		if (withType) {
			willReturn(String.class).given(stringPath).getType();
		}
		return stringPath;
	}

	private static <T extends Number & Comparable<?>> NumberPath<T> numberPath(Class<T> type) {
		NumberPath<?> numberPath = mock(NumberPath.class);
		if (type != null) {
			willReturn(type).given(numberPath).getType();
		}
		return (NumberPath<T>) numberPath;
	}

	private static <T extends Comparable<?>> DatePath<T> datePath(Class<T> type) {
		DatePath<?> datePath = mock(DatePath.class);
		if (type != null) {
			willReturn(type).given(datePath).getType();
		}
		return (DatePath<T>) datePath;
	}

	private static BooleanPath booleanPath(boolean withType) {
		BooleanPath booleanPath = mock(BooleanPath.class);
		if (withType) {
			willReturn(Boolean.class).given(booleanPath).getType();
		}
		return booleanPath;
	}

}
