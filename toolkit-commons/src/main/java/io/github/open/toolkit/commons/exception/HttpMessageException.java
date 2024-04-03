package io.github.open.toolkit.commons.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HttpMessageException extends ExceptionBase {
	
	private static final long serialVersionUID = -3405044885991611202L;

	private final Integer statusCode;
	
	public HttpMessageException(int statusCode) {
		super();
		this.statusCode = statusCode;
	}
	
	@JsonCreator(mode=Mode.PROPERTIES)
	public HttpMessageException(@JsonProperty("statusCode")int statusCode, @JsonProperty("message")String message) {
		super(message);
		this.statusCode = statusCode;
	}

	public Integer getStatusCode() {
		return statusCode;
	}
	
	@Override
	public String toString() {
		String s = getClass().getName();
        s += ": [" + statusCode + "]";
        String message = getLocalizedMessage();
        if (message != null) s += "" + message;
        return s;
	}
}
