package io.qrest.rest.operator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;

@ExtendWith(MockitoExtension.class)
public class BooleanExpressionFactoryTest {

	@ParameterizedTest
	@MethodSource("operatorSymbolSource")
	public void stringPathExpressions_withValidOperators_ShouldBeCreated(String operatorSymbol,
			MockFunction<StringPath, String, BooleanExpression> mockFunction) {

		// given
		StringPath stringPath = mock(StringPath.class);
		BooleanExpression expectedExpression = mock(BooleanExpression.class);
		ConversionService conversionService = mock(ConversionService.class);
		String value = StringUtils.replace(operatorSymbol, "=", "abc");
		mockFunction.mock(stringPath, value, expectedExpression);
		BDDMockito.willReturn(String.class).given(stringPath).getType();
		BooleanExpressionFactory booleanExpressionFactory = new BooleanExpressionFactory(conversionService);

		// when
		BooleanExpression createdExpression = booleanExpressionFactory.create(stringPath, operatorSymbol,
				List.of(value));

		// then
		assertThat(expectedExpression == createdExpression);
		then(conversionService).shouldHaveNoInteractions();

		Mockito.reset(stringPath);
	}

	private static Stream<Arguments> operatorSymbolSource() {
		return Stream.of(
				Arguments.of(OperatorSymbols.EQUAL,
						(MockFunction<StringPath, String, BooleanExpression>) (t, u, r) -> given(t.eq(u))
								.willReturn(r)),
				Arguments.of(OperatorSymbols.NOT_EQUAL, (MockFunction<StringPath, String, BooleanExpression>) (t, u,
						r) -> given(t.ne(u)).willReturn(r)));
	}

}
