package se.uu.ub.cora.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.converter.spider.DataAtomicSpiderToRestConverter;

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
