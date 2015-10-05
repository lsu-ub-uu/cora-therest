package se.uu.ub.cora.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import java.util.Iterator;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.converter.spider.DataGroupSpiderToRestConverter;

public class DataGroupSpiderToRestConverterTest {
	private SpiderDataGroup spiderDataGroup;

	@BeforeMethod
	public void beforeMetod() {
		spiderDataGroup = SpiderDataGroup.withNameInData("nameInData");
	}

	@Test
	public void testToRest() {
		DataGroupSpiderToRestConverter dataGroupSpiderToRestConverter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(spiderDataGroup);
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		assertEquals(restDataGroup.getNameInData(), "nameInData");
	}

	@Test
	public void testToRestWithAttributes() {
		spiderDataGroup.addAttributeByIdWithValue("attributeNameInData", "attributeValue");
		DataGroupSpiderToRestConverter dataGroupSpiderToRestConverter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(spiderDataGroup);
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		String attributeId = restDataGroup.getAttributes().keySet().iterator().next();
		String attributeValue = restDataGroup.getAttributes().get(attributeId);
		assertEquals(attributeValue, "attributeValue");
	}

	@Test
	public void testToRestWithAtomicChild() {
		spiderDataGroup.addChild(SpiderDataAtomic.withNameInDataAndValue("childNameInData", "atomicValue"));
		DataGroupSpiderToRestConverter dataGroupSpiderToRestConverter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(spiderDataGroup);
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		RestDataAtomic restDataAtomic = (RestDataAtomic) restDataGroup.getChildren().iterator()
				.next();
		assertEquals(restDataAtomic.getNameInData(), "childNameInData");
		assertEquals(restDataAtomic.getValue(), "atomicValue");
	}

	@Test
	public void testToRestWithGroupChild() {
		spiderDataGroup.addChild(SpiderDataGroup.withNameInData("childNameInData"));
		DataGroupSpiderToRestConverter dataGroupSpiderToRestConverter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(spiderDataGroup);
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		RestDataGroup restDataGroupChild = (RestDataGroup) restDataGroup.getChildren().iterator()
				.next();
		assertEquals(restDataGroupChild.getNameInData(), "childNameInData");
	}

	@Test
	public void testToRestWithGroupLevelsOfChildren() {
		spiderDataGroup
				.addChild(SpiderDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		SpiderDataGroup dataGroup2 = SpiderDataGroup.withNameInData("childNameInData");
		dataGroup2.addChild(SpiderDataGroup.withNameInData("grandChildNameInData"));
		spiderDataGroup.addChild(dataGroup2);
		DataGroupSpiderToRestConverter dataGroupSpiderToRestConverter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(spiderDataGroup);
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		Iterator<RestDataElement> iterator = restDataGroup.getChildren().iterator();
		Assert.assertTrue(iterator.hasNext(), "dataGroupRest should have at least one child");

		RestDataAtomic dataAtomicChild = (RestDataAtomic) iterator.next();
		Assert.assertEquals(dataAtomicChild.getNameInData(), "atomicNameInData",
				"NameInData should be the one set in the DataAtomic, first child of dataGroup");

		RestDataGroup dataGroupChild = (RestDataGroup) iterator.next();
		Assert.assertEquals(dataGroupChild.getNameInData(), "childNameInData",
				"NameInData should be the one set in dataGroup2");

		Assert.assertEquals(dataGroupChild.getChildren().stream().findAny().get().getNameInData(),
				"grandChildNameInData", "NameInData should be the one set in the child of dataGroup2");
	}

}
