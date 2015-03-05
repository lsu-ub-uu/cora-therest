package epc.therest.data;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.metadataformat.data.DataAtomic;

public class DataAtomicRestTest {
	@Test
	public void testInit(){
		DataAtomicRest dataAtomicRest = new DataAtomicRest("dataId", "value");
		Assert.assertEquals(dataAtomicRest.getDataId(), "dataId");
		Assert.assertEquals(dataAtomicRest.getValue(), "value");
	}
	
	@Test
	public void testCreateWithDataAtomic() {
		DataAtomicRest dataAtomicRest = new DataAtomicRest(new DataAtomic("dataId", "value"));
		Assert.assertEquals(dataAtomicRest.getDataId(), "dataId");
		Assert.assertEquals(dataAtomicRest.getValue(), "value");
	}
}
