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

	// @Test
	// public void testCreateFromSpiderDataAtomic() {
	// SpiderDataAtomic spiderDataAtomic = SpiderDataAtomic.withNameInDataAndValue("nameInData", "value");
	// RestDataAtomic restDataAtomic = RestDataAtomic.fromSpiderDataAtomic(spiderDataAtomic);
	// Assert.assertEquals(restDataAtomic.getNameInData(), "nameInData");
	// Assert.assertEquals(restDataAtomic.getValue(), "value");
	// }
}
