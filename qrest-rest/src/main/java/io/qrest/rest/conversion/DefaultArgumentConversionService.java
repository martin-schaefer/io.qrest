package io.qrest.rest.conversion;

import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;

public class DefaultArgumentConversionService extends DefaultFormattingConversionService implements ArgumentConversionService {

	public DefaultArgumentConversionService() {
		DateTimeFormatterRegistrar dateRegistrar = new DateTimeFormatterRegistrar();
		dateRegistrar.setUseIsoFormat(true);
		dateRegistrar.registerFormatters(this);
	}

}
