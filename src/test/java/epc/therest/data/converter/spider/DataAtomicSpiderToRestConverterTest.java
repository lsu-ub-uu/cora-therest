package epc.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import epc.spider.data.SpiderDataAtomic;
import epc.therest.data.RestDataAtomic;

public class DataAtomicSpiderToRestConverterTest {
	@Test
	public void testToRest() {
		SpiderDataAtomic spiderDataAtomic = SpiderDataAtomic.withNameInDataAndValue("nameInData", "value");
		DataAtomicSpiderToRestConverter atomicSpiderToRestConverter = DataAtomicSpiderToRestConverter
				.fromSpiderDataAtomic(spiderDataAtomic);
		RestDataAtomic restDataAtomic = atomicSpiderToRestConverter.toRest();
		assertEquals(restDataAtomic.getNameInData(), "nameInData");
		assertEquals(restDataAtomic.getValue(), "value");
	}
}
