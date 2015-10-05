package se.uu.ub.cora.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.converter.spider.DataAtomicRestToSpiderConverter;

public class DataAtomicRestToSpiderConverterTest {
	@Test
	public void testToSpider() {
		RestDataAtomic restDataAtomic = RestDataAtomic.withNameInDataAndValue("nameInData", "value");
		DataAtomicRestToSpiderConverter converter = DataAtomicRestToSpiderConverter
				.fromRestDataAtomic(restDataAtomic);
		SpiderDataAtomic spiderDataAtomic = converter.toSpider();
		assertEquals(spiderDataAtomic.getNameInData(), "nameInData");
		assertEquals(spiderDataAtomic.getValue(), "value");
	}
}
