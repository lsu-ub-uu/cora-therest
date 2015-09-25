package epc.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import epc.spider.data.SpiderDataAtomic;
import epc.spider.data.SpiderDataGroup;
import epc.therest.data.RestDataAtomic;
import epc.therest.data.RestDataGroup;

public class DataGroupRestToSpiderConverterTest {
	@Test
	public void testToSpider() {
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("nameInData");
		DataGroupRestToSpiderConverter dataGroupRestToSpiderConverter = DataGroupRestToSpiderConverter
				.fromRestDataGroup(restDataGroup);
		SpiderDataGroup spiderDataGroup = dataGroupRestToSpiderConverter.toSpider();
		assertEquals(spiderDataGroup.getNameInData(), "nameInData");
	}

	@Test
	public void testToSpiderWithAttribute() {
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("nameInData");
		restDataGroup.addAttributeByIdWithValue("attributeId", "attributeValue");
		DataGroupRestToSpiderConverter dataGroupRestToSpiderConverter = DataGroupRestToSpiderConverter
				.fromRestDataGroup(restDataGroup);
		SpiderDataGroup spiderDataGroup = dataGroupRestToSpiderConverter.toSpider();
		String attributeValue = spiderDataGroup.getAttributes().get("attributeId");
		assertEquals(attributeValue, "attributeValue");
	}

	@Test
	public void testToSpiderWithAtomicChild() {
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("nameInData");
		restDataGroup.addChild(RestDataAtomic.withNameInDataAndValue("atomicId", "atomicValue"));
		DataGroupRestToSpiderConverter dataGroupRestToSpiderConverter = DataGroupRestToSpiderConverter
				.fromRestDataGroup(restDataGroup);
		SpiderDataGroup spiderDataGroup = dataGroupRestToSpiderConverter.toSpider();
		SpiderDataAtomic atomicChild = (SpiderDataAtomic) spiderDataGroup.getChildren().get(0);
		assertEquals(atomicChild.getNameInData(), "atomicId");
		assertEquals(atomicChild.getValue(), "atomicValue");
	}

	@Test
	public void testToSpiderWithGroupChild() {
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("nameInData");
		restDataGroup.addChild(RestDataGroup.withNameInData("groupId"));
		DataGroupRestToSpiderConverter dataGroupRestToSpiderConverter = DataGroupRestToSpiderConverter
				.fromRestDataGroup(restDataGroup);
		SpiderDataGroup spiderDataGroup = dataGroupRestToSpiderConverter.toSpider();
		SpiderDataGroup groupChild = (SpiderDataGroup) spiderDataGroup.getChildren().get(0);
		assertEquals(groupChild.getNameInData(), "groupId");
	}

	@Test
	public void testToSpiderWithGroupChildWithAtomicChild() {
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("nameInData");
		RestDataGroup restGroupChild = RestDataGroup.withNameInData("groupId");
		restGroupChild.addChild(RestDataAtomic.withNameInDataAndValue("grandChildAtomicId",
				"grandChildAtomicValue"));
		restGroupChild.addAttributeByIdWithValue("groupChildAttributeId",
				"groupChildAttributeValue");
		restDataGroup.addChild(restGroupChild);

		DataGroupRestToSpiderConverter dataGroupRestToSpiderConverter = DataGroupRestToSpiderConverter
				.fromRestDataGroup(restDataGroup);
		SpiderDataGroup spiderDataGroup = dataGroupRestToSpiderConverter.toSpider();
		SpiderDataGroup groupChild = (SpiderDataGroup) spiderDataGroup.getChildren().get(0);
		SpiderDataAtomic grandChildAtomic = (SpiderDataAtomic) groupChild.getChildren().get(0);

		assertEquals(grandChildAtomic.getNameInData(), "grandChildAtomicId");

		String groupChildAttributeValue = restGroupChild.getAttributes().get(
				"groupChildAttributeId");
		assertEquals(groupChildAttributeValue, "groupChildAttributeValue");
	}

}
