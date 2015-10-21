package se.uu.ub.cora.therest.data;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RestDataAtomicTest {
	private RestDataAtomic restDataAtomic;

	@BeforeMethod
	public void setUp() {
		restDataAtomic = RestDataAtomic.withNameInDataAndValue("nameInData", "value");

	}

	@Test
	public void testInit() {
		assertEquals(restDataAtomic.getNameInData(), "nameInData");
		assertEquals(restDataAtomic.getValue(), "value");
	}

	@Test
	public void testInitWithRepeatId() {
		restDataAtomic.setRepeatId("x1");
		assertEquals(restDataAtomic.getNameInData(), "nameInData");
		assertEquals(restDataAtomic.getValue(), "value");
		assertEquals(restDataAtomic.getRepeatId(), "x1");

	}
}
