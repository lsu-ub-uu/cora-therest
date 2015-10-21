package se.uu.ub.cora.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.therest.data.RestDataAtomic;

public class DataAtomicRestToSpiderConverterTest {
	private RestDataAtomic restDataAtomic;
	private DataAtomicRestToSpiderConverter converter;

	@BeforeMethod
	public void setUp() {
		restDataAtomic = RestDataAtomic.withNameInDataAndValue("nameInData", "value");
		converter = DataAtomicRestToSpiderConverter.fromRestDataAtomic(restDataAtomic);

	}

	@Test
	public void testToSpider() {
		SpiderDataAtomic spiderDataAtomic = converter.toSpider();
		assertEquals(spiderDataAtomic.getNameInData(), "nameInData");
		assertEquals(spiderDataAtomic.getValue(), "value");
	}

	@Test
	public void testToSpiderWithRepeatId() {
		restDataAtomic.setRepeatId("x3");
		SpiderDataAtomic spiderDataAtomic = converter.toSpider();
		assertEquals(spiderDataAtomic.getNameInData(), "nameInData");
		assertEquals(spiderDataAtomic.getValue(), "value");
		assertEquals(spiderDataAtomic.getRepeatId(), "x3");
	}
}
