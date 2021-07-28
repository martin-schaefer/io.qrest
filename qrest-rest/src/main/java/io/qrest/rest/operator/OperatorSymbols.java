package io.qrest.rest.operator;

public interface OperatorSymbols {

	// Basic operations
	static final String EQUAL = "==";
	static final String NOT_EQUAL = "!=";
	// Basic multiple value operations
	static final String IN = "=in=";
	static final String NOT_IN = "=out=";
	// Greater/Less than operations
	static final String GREATER_THAN = "=gt=";
	static final String GREATER_THAN_ALT = ">";
	static final String GREATER_THAN_OR_EQUAL = "=ge=";
	static final String GREATER_THAN_OR_EQUAL_ALT = ">=";
	static final String LESS_THAN = "=lt=";
	static final String LESS_THAN_ALT = "<";
	static final String LESS_THAN_OR_EQUAL = "=le=";
	static final String LESS_THAN_OR_EQUAL_ALT = "<=";
	// String operations
	static final String LIKE = "=lk=";
	static final String LIKE_CASE_INSENSITIVE = "=lki=";

	// Predefined values
	static final String NULL_VALUE = "null";

}
