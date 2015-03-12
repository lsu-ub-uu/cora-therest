package epc.therest.data;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.metadataformat.data.DataAtomic;

public class RestDataAtomicTest {
	@Test
	public void testInit() {
		RestDataAtomic restDataAtomic = RestDataAtomic.withDataIdAndValue("dataId", "value");
		Assert.assertEquals(restDataAtomic.getDataId(), "dataId");
		Assert.assertEquals(restDataAtomic.getValue(), "value");
	}

	@Test
	public void testCreateFromDataAtomic() {
		DataAtomic dataAtomic = DataAtomic.withDataIdAndValue("dataId", "value");
		RestDataAtomic restDataAtomic = RestDataAtomic.fromDataAtomic(dataAtomic);
		Assert.assertEquals(restDataAtomic.getDataId(), "dataId");
		Assert.assertEquals(restDataAtomic.getValue(), "value");
	}
}
