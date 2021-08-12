package io.qrest.rest.operator;

import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;

public class DefaultArgumentConversionService extends DefaultFormattingConversionService implements ArgumentConversionService {

	public DefaultArgumentConversionService() {
		DateTimeFormatterRegistrar dateRegistrar = new DateTimeFormatterRegistrar();
		dateRegistrar.setUseIsoFormat(true);
		dateRegistrar.registerFormatters(this);
	}

}
