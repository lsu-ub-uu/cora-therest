package epc.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import java.util.Iterator;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.spider.data.Action;
import epc.spider.data.SpiderDataAtomic;
import epc.spider.data.SpiderDataGroup;
import epc.therest.data.ActionLink;
import epc.therest.data.RestDataAtomic;
import epc.therest.data.RestDataElement;
import epc.therest.data.RestDataGroup;
import epc.therest.data.converter.ConverterException;

public class DataGroupSpiderToRestConverterTest {
	private SpiderDataGroup spiderDataGroup;

	@BeforeMethod
	public void beforeMetod() {
		spiderDataGroup = SpiderDataGroup.withDataId("dataId");
	}

	@Test
	public void testToRest() {
		DataGroupSpiderToRestConverter dataGroupSpiderToRestConverter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(spiderDataGroup);
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		assertEquals(restDataGroup.getDataId(), "dataId");
	}

	@Test
	public void testToRestWithAttributes() {
		spiderDataGroup.addAttributeByIdWithValue("attributeDataId", "attributeValue");
		DataGroupSpiderToRestConverter dataGroupSpiderToRestConverter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(spiderDataGroup);
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		String attributeId = restDataGroup.getAttributes().keySet().iterator().next();
		String attributeValue = restDataGroup.getAttributes().get(attributeId);
		assertEquals(attributeValue, "attributeValue");
	}

	@Test
	public void testToRestWithAtomicChild() {
		spiderDataGroup.addChild(SpiderDataAtomic.withDataIdAndValue("childDataId", "atomicValue"));
		DataGroupSpiderToRestConverter dataGroupSpiderToRestConverter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(spiderDataGroup);
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		RestDataAtomic restDataAtomic = (RestDataAtomic) restDataGroup.getChildren().iterator()
				.next();
		assertEquals(restDataAtomic.getDataId(), "childDataId");
		assertEquals(restDataAtomic.getValue(), "atomicValue");
	}

	@Test
	public void testToRestWithGroupChild() {
		spiderDataGroup.addChild(SpiderDataGroup.withDataId("childDataId"));
		DataGroupSpiderToRestConverter dataGroupSpiderToRestConverter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(spiderDataGroup);
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		RestDataGroup restDataGroupChild = (RestDataGroup) restDataGroup.getChildren().iterator()
				.next();
		assertEquals(restDataGroupChild.getDataId(), "childDataId");
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToRestWithActionLinkNoRecordInfo() {
		spiderDataGroup.addAction(Action.READ);
		spiderDataGroup
				.addChild(SpiderDataAtomic.withDataIdAndValue("atomicDataId", "atomicValue"));
		DataGroupSpiderToRestConverter dataGroupSpiderToRestConverter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(spiderDataGroup);
		dataGroupSpiderToRestConverter.toRest();
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToRestWithActionLinkNoId() {
		spiderDataGroup.addAction(Action.READ);
		DataGroupSpiderToRestConverter dataGroupSpiderToRestConverter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(spiderDataGroup);

		SpiderDataGroup recordInfo = SpiderDataGroup.withDataId("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withDataIdAndValue("type", "place"));
		recordInfo.addChild(SpiderDataAtomic.withDataIdAndValue("createdBy", "userId"));

		spiderDataGroup.addChild(recordInfo);

		dataGroupSpiderToRestConverter.toRest();
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToRestWithActionLinkNoType() {
		spiderDataGroup.addAction(Action.READ);
		DataGroupSpiderToRestConverter dataGroupSpiderToRestConverter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(spiderDataGroup);

		SpiderDataGroup recordInfo = SpiderDataGroup.withDataId("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withDataIdAndValue("id", "place:0001"));
		recordInfo.addChild(SpiderDataAtomic.withDataIdAndValue("createdBy", "userId"));

		spiderDataGroup.addChild(recordInfo);

		dataGroupSpiderToRestConverter.toRest();
	}

	@Test
	public void testLinkConvertionREAD() {
		spiderDataGroup.addAction(Action.READ);

		SpiderDataGroup recordInfo = SpiderDataGroup.withDataId("recordInfo");
		// id
		recordInfo.addChild(SpiderDataAtomic.withDataIdAndValue("id", "place:0001"));
		recordInfo.addChild(SpiderDataAtomic.withDataIdAndValue("type", "place"));
		recordInfo.addChild(SpiderDataAtomic.withDataIdAndValue("createdBy", "userId"));

		spiderDataGroup.addChild(recordInfo);

		DataGroupSpiderToRestConverter dataGroupSpiderToRestConverter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(spiderDataGroup);
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		Set<ActionLink> actionLinks = restDataGroup.getActionLinks();
		Iterator<ActionLink> iterator = actionLinks.iterator();
		ActionLink actionLink = iterator.next();
		assertEquals(actionLink.getAction(), Action.READ);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/place/place:0001");
		assertEquals(actionLink.getRequestMethod(), "GET");
	}

	@Test
	public void testLinkConvertionUPDATE() {
		spiderDataGroup.addAction(Action.UPDATE);

		SpiderDataGroup recordInfo = SpiderDataGroup.withDataId("recordInfo");
		// id
		recordInfo.addChild(SpiderDataAtomic.withDataIdAndValue("id", "place:0001"));
		recordInfo.addChild(SpiderDataAtomic.withDataIdAndValue("type", "place"));
		recordInfo.addChild(SpiderDataAtomic.withDataIdAndValue("createdBy", "userId"));

		spiderDataGroup.addChild(recordInfo);

		DataGroupSpiderToRestConverter dataGroupSpiderToRestConverter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(spiderDataGroup);
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		Set<ActionLink> actionLinks = restDataGroup.getActionLinks();
		Iterator<ActionLink> iterator = actionLinks.iterator();
		ActionLink actionLink = iterator.next();
		assertEquals(actionLink.getAction(), Action.UPDATE);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/place/place:0001");
		assertEquals(actionLink.getRequestMethod(), "POST");

	}

	@Test
	public void testToRestWithGroupLevelsOfChildren() {
		spiderDataGroup
				.addChild(SpiderDataAtomic.withDataIdAndValue("atomicDataId", "atomicValue"));
		SpiderDataGroup dataGroup2 = SpiderDataGroup.withDataId("childDataId");
		dataGroup2.addChild(SpiderDataGroup.withDataId("grandChildDataId"));
		spiderDataGroup.addChild(dataGroup2);
		DataGroupSpiderToRestConverter dataGroupSpiderToRestConverter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(spiderDataGroup);
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		Iterator<RestDataElement> iterator = restDataGroup.getChildren().iterator();
		Assert.assertTrue(iterator.hasNext(), "dataGroupRest should have at least one child");

		RestDataAtomic dataAtomicChild = (RestDataAtomic) iterator.next();
		Assert.assertEquals(dataAtomicChild.getDataId(), "atomicDataId",
				"DataId should be the one set in the DataAtomic, first child of dataGroup");

		RestDataGroup dataGroupChild = (RestDataGroup) iterator.next();
		Assert.assertEquals(dataGroupChild.getDataId(), "childDataId",
				"DataId should be the one set in dataGroup2");

		Assert.assertEquals(dataGroupChild.getChildren().stream().findAny().get().getDataId(),
				"grandChildDataId", "DataId should be the one set in the child of dataGroup2");
	}

}
