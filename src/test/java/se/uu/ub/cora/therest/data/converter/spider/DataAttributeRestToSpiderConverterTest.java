package se.uu.ub.cora.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.SpiderDataAttribute;
import se.uu.ub.cora.therest.data.RestDataAttribute;
import se.uu.ub.cora.therest.data.converter.spider.DataAttributeRestToSpiderConverter;

public class DataAttributeRestToSpiderConverterTest {
	@Test
	public void testToSpider() {
		RestDataAttribute restDataAttribute = RestDataAttribute.withNameInDataAndValue("nameInData", "value");
		DataAttributeRestToSpiderConverter converter = DataAttributeRestToSpiderConverter
				.fromRestDataAttribute(restDataAttribute);
		SpiderDataAttribute spiderDataAttribute = converter.toSpider();
		assertEquals(spiderDataAttribute.getNameInData(), "nameInData");
		assertEquals(spiderDataAttribute.getValue(), "value");
	}
}
