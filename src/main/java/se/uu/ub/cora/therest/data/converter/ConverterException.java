package se.uu.ub.cora.therest.data.converter;

public class ConverterException extends RuntimeException {

	private static final long serialVersionUID = 6721892121477803630L;

	public ConverterException(String message) {
		super(message);
	}

	public ConverterException(String message, Exception exception) {
		super(message, exception);
	}

}
