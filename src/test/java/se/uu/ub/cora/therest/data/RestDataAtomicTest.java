package se.uu.ub.cora.therest.data;

import org.testng.Assert;
import org.testng.annotations.Test;

import se.uu.ub.cora.therest.data.RestDataAtomic;

public class RestDataAtomicTest {
	@Test
	public void testInit() {
		RestDataAtomic restDataAtomic = RestDataAtomic.withNameInDataAndValue("nameInData", "value");
		Assert.assertEquals(restDataAtomic.getNameInData(), "nameInData");
		Assert.assertEquals(restDataAtomic.getValue(), "value");
	}
}
