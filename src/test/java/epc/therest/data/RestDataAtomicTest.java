package epc.therest.data;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RestDataAtomicTest {
	@Test
	public void testInit() {
		RestDataAtomic restDataAtomic = RestDataAtomic.withDataIdAndValue("dataId", "value");
		Assert.assertEquals(restDataAtomic.getDataId(), "dataId");
		Assert.assertEquals(restDataAtomic.getValue(), "value");
	}

	// @Test
	// public void testCreateFromSpiderDataAtomic() {
	// SpiderDataAtomic spiderDataAtomic = SpiderDataAtomic.withDataIdAndValue("dataId", "value");
	// RestDataAtomic restDataAtomic = RestDataAtomic.fromSpiderDataAtomic(spiderDataAtomic);
	// Assert.assertEquals(restDataAtomic.getDataId(), "dataId");
	// Assert.assertEquals(restDataAtomic.getValue(), "value");
	// }
}
