package epc.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import epc.spider.data.Action;
import epc.spider.data.SpiderDataAtomic;
import epc.spider.data.SpiderDataGroup;
import epc.therest.data.ActionLink;
import epc.therest.data.RestDataAtomic;
import epc.therest.data.RestDataGroup;

public class DataGroupRestToSpiderConverterTest {
	@Test
	public void testToSpider() {
		RestDataGroup restDataGroup = RestDataGroup.withDataId("dataId");
		DataGroupRestToSpiderConverter dataGroupRestToSpiderConverter = DataGroupRestToSpiderConverter
				.fromRestDataGroup(restDataGroup);
		SpiderDataGroup spiderDataGroup = dataGroupRestToSpiderConverter.toSpider();
		assertEquals(spiderDataGroup.getDataId(), "dataId");
	}

	@Test
	public void testToSpiderWithAttribute() {
		RestDataGroup restDataGroup = RestDataGroup.withDataId("dataId");
		restDataGroup.addAttributeByIdWithValue("attributeId", "attributeValue");
		DataGroupRestToSpiderConverter dataGroupRestToSpiderConverter = DataGroupRestToSpiderConverter
				.fromRestDataGroup(restDataGroup);
		SpiderDataGroup spiderDataGroup = dataGroupRestToSpiderConverter.toSpider();
		String attributeValue = spiderDataGroup.getAttributes().get("attributeId");
		assertEquals(attributeValue, "attributeValue");
	}

	@Test
	public void testToSpiderWithActionLinks() {
		RestDataGroup restDataGroup = RestDataGroup.withDataId("dataId");
		restDataGroup.addActionLink(ActionLink.withAction(Action.READ));
		DataGroupRestToSpiderConverter dataGroupRestToSpiderConverter = DataGroupRestToSpiderConverter
				.fromRestDataGroup(restDataGroup);
		SpiderDataGroup spiderDataGroup = dataGroupRestToSpiderConverter.toSpider();
		Action action = spiderDataGroup.getActions().iterator().next();
		assertEquals(action, Action.READ);
	}

	@Test
	public void testToSpiderWithAtomicChild() {
		RestDataGroup restDataGroup = RestDataGroup.withDataId("dataId");
		restDataGroup.addChild(RestDataAtomic.withDataIdAndValue("atomicId", "atomicValue"));
		DataGroupRestToSpiderConverter dataGroupRestToSpiderConverter = DataGroupRestToSpiderConverter
				.fromRestDataGroup(restDataGroup);
		SpiderDataGroup spiderDataGroup = dataGroupRestToSpiderConverter.toSpider();
		SpiderDataAtomic atomicChild = (SpiderDataAtomic) spiderDataGroup.getChildren().get(0);
		assertEquals(atomicChild.getDataId(), "atomicId");
		assertEquals(atomicChild.getValue(), "atomicValue");
	}

	@Test
	public void testToSpiderWithGroupChild() {
		RestDataGroup restDataGroup = RestDataGroup.withDataId("dataId");
		restDataGroup.addChild(RestDataGroup.withDataId("groupId"));
		DataGroupRestToSpiderConverter dataGroupRestToSpiderConverter = DataGroupRestToSpiderConverter
				.fromRestDataGroup(restDataGroup);
		SpiderDataGroup spiderDataGroup = dataGroupRestToSpiderConverter.toSpider();
		SpiderDataGroup groupChild = (SpiderDataGroup) spiderDataGroup.getChildren().get(0);
		assertEquals(groupChild.getDataId(), "groupId");
	}

	@Test
	public void testToSpiderWithGroupChildWithAtomicChild() {
		RestDataGroup restDataGroup = RestDataGroup.withDataId("dataId");
		RestDataGroup restGroupChild = RestDataGroup.withDataId("groupId");
		restGroupChild.addChild(RestDataAtomic.withDataIdAndValue("grandChildAtomicId",
				"grandChildAtomicValue"));
		restGroupChild.addAttributeByIdWithValue("groupChildAttributeId",
				"groupChildAttributeValue");
		restDataGroup.addChild(restGroupChild);

		DataGroupRestToSpiderConverter dataGroupRestToSpiderConverter = DataGroupRestToSpiderConverter
				.fromRestDataGroup(restDataGroup);
		SpiderDataGroup spiderDataGroup = dataGroupRestToSpiderConverter.toSpider();
		SpiderDataGroup groupChild = (SpiderDataGroup) spiderDataGroup.getChildren().get(0);
		SpiderDataAtomic grandChildAtomic = (SpiderDataAtomic) groupChild.getChildren().get(0);

		assertEquals(grandChildAtomic.getDataId(), "grandChildAtomicId");

		String groupChildAttributeValue = restGroupChild.getAttributes().get(
				"groupChildAttributeId");
		assertEquals(groupChildAttributeValue, "groupChildAttributeValue");
	}

}
