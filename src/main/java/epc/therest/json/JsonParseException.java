package epc.therest.json;

public class JsonParseException extends RuntimeException {

	private static final long serialVersionUID = 1854934226186984698L;

	public JsonParseException(String message, Exception exception) {
		super(message, exception);
	}

	public JsonParseException(String message) {
		super(message);
	}

}
