package io.qrest.rest.operator;

import static io.qrest.rest.operator.OperatorSymbols.EQUAL;
import static io.qrest.rest.operator.OperatorSymbols.IN;
import static io.qrest.rest.operator.OperatorSymbols.LIKE;
import static io.qrest.rest.operator.OperatorSymbols.LIKE_CASE_INSENSITIVE;
import static io.qrest.rest.operator.OperatorSymbols.NOT_EQUAL;
import static io.qrest.rest.operator.OperatorSymbols.NOT_IN;
import static org.apache.commons.lang3.StringUtils.replace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;

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

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;

@ExtendWith(MockitoExtension.class)
public class BooleanExpressionFactoryTest {

	private static final BooleanExpression expectedExpression = mock(BooleanExpression.class);

	@ParameterizedTest
	@MethodSource("operatorSymbolSource")
	public void stringPathExpressions_withValidOperators_ShouldBeCreated(String operatorSymbol,
			Function<String, Path<?>> mockFunction,
			BiConsumer<Path<?>, String> verifyFunction) {

		// given
		ConversionService conversionService = mock(ConversionService.class);
		String value = replace(operatorSymbol, "=", "abc");
		Path<?> stringPath = mockFunction.apply(value);
		BooleanExpressionFactory booleanExpressionFactory = new BooleanExpressionFactory(conversionService);

		// when
		BooleanExpression createdExpression = booleanExpressionFactory.create(stringPath, operatorSymbol,
				List.of(value));

		// then
		assertThat(createdExpression).isNotNull();
		verifyFunction.accept(stringPath, value);
		then(stringPath).shouldHaveNoMoreInteractions();
		then(conversionService).shouldHaveNoInteractions();
	}

	private static Stream<Arguments> operatorSymbolSource() {
		return Stream.of(Arguments.of(EQUAL, (Function<String, Path<?>>) (p) -> {
			StringPath mock = stringPath(true);
			given(mock.eq(p)).willReturn(expectedExpression);
			return mock;
		}, (BiConsumer<StringPath, String>) (m, p) -> {
			then(m).should().getType();
			then(m).should().eq(p);
		}), Arguments.of(NOT_EQUAL, (Function<String, Path<?>>) (p) -> {
			StringPath mock = stringPath(true);
			given(mock.ne(p)).willReturn(expectedExpression);
			return mock;
		}, (BiConsumer<StringPath, String>) (m, p) -> {
			then(m).should().getType();
			then(m).should().ne(p);
		}), Arguments.of(IN, (Function<String, Path<?>>) (p) -> {
			StringPath mock = stringPath(true);
			given(mock.in(List.of(p))).willReturn(expectedExpression);
			return mock;
		}, (BiConsumer<StringPath, String>) (m, p) -> {
			then(m).should().getType();
			then(m).should().in(List.of(p));
		}), Arguments.of(NOT_IN, (Function<String, Path<?>>) (p) -> {
			StringPath mock = stringPath(true);
			given(mock.notIn(List.of(p))).willReturn(expectedExpression);
			return mock;
		}, (BiConsumer<StringPath, String>) (m, p) -> {
			then(m).should().getType();
			then(m).should().notIn(List.of(p));
		}), Arguments.of(LIKE, (Function<String, Path<?>>) (p) -> {
			StringPath mock = stringPath(false);
			given(mock.like(p)).willReturn(expectedExpression);
			return mock;
		}, (BiConsumer<StringPath, String>) (m, p) -> {
			then(m).should().like(p);
		}),
				Arguments.of(LIKE_CASE_INSENSITIVE, (Function<String, Path<?>>) (p) -> {
					StringPath mock = stringPath(false);
					given(mock.likeIgnoreCase(p)).willReturn(expectedExpression);
					return mock;
				}, (BiConsumer<StringPath, String>) (m, p) -> {
					then(m).should().likeIgnoreCase(p);
				}));
	}

	private static StringPath stringPath(boolean withType) {
		StringPath stringPath = mock(StringPath.class);
		if (withType) {
			willReturn(String.class).given(stringPath).getType();
		}
		return stringPath;
	}
}
