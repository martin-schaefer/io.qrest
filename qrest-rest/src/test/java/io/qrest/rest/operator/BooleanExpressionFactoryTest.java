package io.qrest.rest.operator;

import static io.qrest.rest.operator.OperatorSymbols.EQUAL;
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

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;

@ExtendWith(MockitoExtension.class)
public class BooleanExpressionFactoryTest {

	@ParameterizedTest
	@MethodSource("operatorSymbolSource")
	public void stringPathExpressions_withValidOperators_ShouldBeCreated(String operatorSymbol,
			MockFunction<StringPath, String, BooleanExpression> mockFunction,
			BiConsumer<StringPath, String> verifyFunction) {

		// given
		StringPath stringPath = mock(StringPath.class);
		BooleanExpression expectedExpression = mock(BooleanExpression.class);
		ConversionService conversionService = mock(ConversionService.class);
		String value = StringUtils.replace(operatorSymbol, "=", "abc");
		mockFunction.mock(stringPath, value, expectedExpression);
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
		return Stream.of(Arguments.of(EQUAL, (MockFunction<StringPath, String, BooleanExpression>) (m, p, r) -> {
			willReturn(String.class).given(m).getType();
			given(m.eq(p)).willReturn(r);
		}, (BiConsumer<StringPath, String>) (m, p) -> {
			then(m).should().getType();
			then(m).should().eq(p);
		}), Arguments.of(NOT_EQUAL, (MockFunction<StringPath, String, BooleanExpression>) (m, p, r) -> {
			willReturn(String.class).given(m).getType();
			given(m.ne(p)).willReturn(r);
		}, (BiConsumer<StringPath, String>) (m, p) -> {
			then(m).should().getType();
			then(m).should().ne(p);
		}), Arguments.of(IN, (MockFunction<StringPath, String, BooleanExpression>) (m, p, r) -> {
			willReturn(String.class).given(m).getType();
			given(m.in(List.of(p))).willReturn(r);
		}, (BiConsumer<StringPath, String>) (m, p) -> {
			then(m).should().getType();
			then(m).should().in(List.of(p));
		}), Arguments.of(NOT_IN, (MockFunction<StringPath, String, BooleanExpression>) (m, p, r) -> {
			willReturn(String.class).given(m).getType();
			given(m.notIn(List.of(p))).willReturn(r);
		}, (BiConsumer<StringPath, String>) (m, p) -> {
			then(m).should().getType();
			then(m).should().notIn(List.of(p));
		}), Arguments.of(LIKE,
				(MockFunction<StringPath, String, BooleanExpression>) (m, p, r) -> given(m.like(p)).willReturn(r),
				(BiConsumer<StringPath, String>) (m, p) -> then(m).should().like(p)),
				Arguments.of(LIKE_CASE_INSENSITIVE,
						(MockFunction<StringPath, String, BooleanExpression>) (m, p, r) -> given(m.likeIgnoreCase(p))
								.willReturn(r),
						(BiConsumer<StringPath, String>) (m, p) -> then(m).should().likeIgnoreCase(p))

		);
	}

}
