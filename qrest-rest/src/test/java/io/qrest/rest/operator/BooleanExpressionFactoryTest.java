package io.qrest.rest.operator;

import static io.qrest.rest.operator.OperatorSymbols.EQUAL;
import static io.qrest.rest.operator.OperatorSymbols.GREATER_THAN;
import static io.qrest.rest.operator.OperatorSymbols.IN;
import static io.qrest.rest.operator.OperatorSymbols.LIKE;
import static io.qrest.rest.operator.OperatorSymbols.LIKE_CASE_INSENSITIVE;
import static io.qrest.rest.operator.OperatorSymbols.NOT_EQUAL;
import static io.qrest.rest.operator.OperatorSymbols.NOT_IN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import com.querydsl.core.types.dsl.BooleanExpression;
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
			NumberPath<?> mock = numberPath(true);
			given(mock.gt(p)).willReturn(expectedExpression);
			return mock;
		}, (BiConsumer<NumberPath<?>, Integer>) (m, p) -> {
			then(m).should(times(2)).getType();
			then(m).should().gt(p);
		}));
	}

	private static StringPath stringPath(boolean withType) {
		StringPath stringPath = mock(StringPath.class);
		if (withType) {
			willReturn(String.class).given(stringPath).getType();
		}
		return stringPath;
	}

	private static NumberPath<?> numberPath(boolean withType) {
		NumberPath<?> numberPath = mock(NumberPath.class);
		if (withType) {
			willReturn(Integer.class).given(numberPath).getType();
		}
		return numberPath;
	}

}
