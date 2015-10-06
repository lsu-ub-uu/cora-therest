package se.uu.ub.cora.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.SpiderDataAttribute;
import se.uu.ub.cora.therest.data.RestDataAttribute;
import se.uu.ub.cora.therest.data.converter.spider.DataAttributeSpiderToRestConverter;

public class DataAttributeSpiderToRestConverterTest {
	@Test
	public void testToRest() {
		SpiderDataAttribute spiderDataAttribute = SpiderDataAttribute.withNameInDataAndValue("nameInData", "value");
		DataAttributeSpiderToRestConverter atomicSpiderToRestConverter = DataAttributeSpiderToRestConverter
				.fromSpiderDataAttribute(spiderDataAttribute);
		RestDataAttribute restDataAttribute = atomicSpiderToRestConverter.toRest();
		assertEquals(restDataAttribute.getNameInData(), "nameInData");
		assertEquals(restDataAttribute.getValue(), "value");
	}
}
