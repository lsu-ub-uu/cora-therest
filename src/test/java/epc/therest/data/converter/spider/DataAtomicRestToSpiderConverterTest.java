package epc.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import epc.spider.data.SpiderDataAtomic;
import epc.therest.data.RestDataAtomic;

public class DataAtomicRestToSpiderConverterTest {
	@Test
	public void testToSpider() {
		RestDataAtomic restDataAtomic = RestDataAtomic.withDataIdAndValue("dataId", "value");
		DataAtomicRestToSpiderConverter converter = DataAtomicRestToSpiderConverter
				.fromRestDataAtomic(restDataAtomic);
		SpiderDataAtomic spiderDataAtomic = converter.toSpider();
		assertEquals(spiderDataAtomic.getDataId(), "dataId");
		assertEquals(spiderDataAtomic.getValue(), "value");
	}
}
