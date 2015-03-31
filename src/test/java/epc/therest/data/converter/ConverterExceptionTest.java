package epc.therest.data.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class ConverterExceptionTest {
	@Test
	public void testInit() {
		Exception exception = new RuntimeException("e");
		ConverterException converterException = new ConverterException("message", exception);
		assertEquals(converterException.getMessage(), "message");
		assertEquals(converterException.getCause(), exception);
	}

	@Test
	public void testInitMessage() {
		ConverterException converterException = new ConverterException("message");
		assertEquals(converterException.getMessage(), "message");
	}

}
