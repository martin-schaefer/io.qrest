package io.qrest.rest.operator;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class QRestOperatorException extends RuntimeException {

	private static final long serialVersionUID = 1989905353963464252L;

	public QRestOperatorException(String message) {
		super(message);
	}

	public QRestOperatorException(String message, Throwable cause) {
		super(message, cause);
	}

}
