package io.qrest.rest.operator;

@FunctionalInterface
public interface MockFunction<M, P, R> {

	public void mock(M mock, P parameter, R returnValue);
}
