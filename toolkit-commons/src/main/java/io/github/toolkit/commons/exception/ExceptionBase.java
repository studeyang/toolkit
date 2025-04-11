package io.github.toolkit.commons.exception;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class ExceptionBase extends RuntimeException {
	
	private static final long serialVersionUID = 5288617473258285849L;

	public ExceptionBase() {
		super("");
	}
	public ExceptionBase(String message) {
		super(message);
	}
}
