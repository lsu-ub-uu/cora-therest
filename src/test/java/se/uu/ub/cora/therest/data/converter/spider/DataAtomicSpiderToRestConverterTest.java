package se.uu.ub.cora.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.therest.data.RestDataAtomic;

public class DataAtomicSpiderToRestConverterTest {
	private SpiderDataAtomic spiderDataAtomic;
	private DataAtomicSpiderToRestConverter atomicSpiderToRestConverter;

	@BeforeMethod
	public void setUp() {
		spiderDataAtomic = SpiderDataAtomic.withNameInDataAndValue("nameInData", "value");
		atomicSpiderToRestConverter = DataAtomicSpiderToRestConverter
				.fromSpiderDataAtomic(spiderDataAtomic);

	}

	@Test
	public void testToRest() {
		RestDataAtomic restDataAtomic = atomicSpiderToRestConverter.toRest();
		assertEquals(restDataAtomic.getNameInData(), "nameInData");
		assertEquals(restDataAtomic.getValue(), "value");
	}

	@Test
	public void testToRestWithRepeatId() {
		spiderDataAtomic.setRepeatId("e4");
		RestDataAtomic restDataAtomic = atomicSpiderToRestConverter.toRest();
		assertEquals(restDataAtomic.getNameInData(), "nameInData");
		assertEquals(restDataAtomic.getValue(), "value");
		assertEquals(restDataAtomic.getRepeatId(), "e4");
	}
}
