package epc.therest.data.converter.spider;

import epc.spider.data.Action;
import epc.spider.data.SpiderDataAtomic;
import epc.spider.data.SpiderDataGroup;
import epc.spider.data.SpiderDataRecord;
import epc.therest.data.ActionLink;
import epc.therest.data.RestDataGroup;
import epc.therest.data.RestDataRecord;
import epc.therest.data.converter.ConverterException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class DataRecordSpiderToRestConterterTest {
	private String baseURL = "http://localhost:8080/therest/rest/record/";

	@Test
	public void testToRest() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		SpiderDataRecord spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		DataRecordSpiderToRestConverter dataRecordSpiderToRestConverter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL);
		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();
		RestDataGroup restDataGroup = restDataRecord.getRestDataGroup();
		assertEquals(restDataGroup.getNameInData(), "groupId");
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToRestWithActionLinkNoRecordInfoButOtherChild() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		spiderDataGroup.addChild(SpiderDataAtomic.withNameInDataAndValue("type", "place"));
		SpiderDataRecord spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		spiderDataRecord.addAction(Action.READ);

		DataRecordSpiderToRestConverter dataRecordSpiderToRestConverter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL);
		dataRecordSpiderToRestConverter.toRest();
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToRestWithActionLinkNoRecordInfo() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		SpiderDataRecord spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		spiderDataRecord.addAction(Action.READ);
		DataRecordSpiderToRestConverter dataRecordSpiderToRestConverter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL);
		dataRecordSpiderToRestConverter.toRest();
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToRestWithActionLinkNoId() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		SpiderDataRecord spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		spiderDataRecord.addAction(Action.READ);

		SpiderDataGroup recordInfo = SpiderDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("type", "place"));
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("createdBy", "userId"));
		spiderDataGroup.addChild(recordInfo);

		DataRecordSpiderToRestConverter dataRecordSpiderToRestConverter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL);
		dataRecordSpiderToRestConverter.toRest();
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToRestWithActionLinkNoType() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		SpiderDataRecord spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		spiderDataRecord.addAction(Action.READ);

		SpiderDataGroup recordInfo = SpiderDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("id", "place:0001"));
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("createdBy", "userId"));
		spiderDataGroup.addChild(recordInfo);

		DataRecordSpiderToRestConverter dataRecordSpiderToRestConverter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL);
		dataRecordSpiderToRestConverter.toRest();
	}

	@Test
	public void testToRestWithActionLinkREAD() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		SpiderDataRecord spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		spiderDataRecord.addAction(Action.READ);

		SpiderDataGroup recordInfo = SpiderDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("id", "place:0001"));
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("type", "place"));
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("createdBy", "userId"));
		spiderDataGroup.addChild(recordInfo);

		DataRecordSpiderToRestConverter dataRecordSpiderToRestConverter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL);
		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();
		ActionLink actionLink = restDataRecord.getActionLink("read");
		assertEquals(actionLink.getAction(), Action.READ);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/place/place:0001");
		assertEquals(actionLink.getRequestMethod(), "GET");
	}

	@Test
	public void testToRestWithActionLinkUPDATE() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		SpiderDataRecord spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		spiderDataRecord.addAction(Action.UPDATE);

		SpiderDataGroup recordInfo = SpiderDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("id", "place:0001"));
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("type", "place"));
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("createdBy", "userId"));
		spiderDataGroup.addChild(recordInfo);

		DataRecordSpiderToRestConverter dataRecordSpiderToRestConverter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL);
		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();
		ActionLink actionLink = restDataRecord.getActionLink("update");
		assertEquals(actionLink.getAction(), Action.UPDATE);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/place/place:0001");
		assertEquals(actionLink.getRequestMethod(), "POST");
	}

	@Test
	public void testToRestWithActionLinkDELETE() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		SpiderDataRecord spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		spiderDataRecord.addAction(Action.DELETE);

		SpiderDataGroup recordInfo = SpiderDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("id", "place:0001"));
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("type", "place"));
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("createdBy", "userId"));
		spiderDataGroup.addChild(recordInfo);

		DataRecordSpiderToRestConverter dataRecordSpiderToRestConverter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL);
		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();
		ActionLink actionLink = restDataRecord.getActionLink("delete");
		assertEquals(actionLink.getAction(), Action.DELETE);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/place/place:0001");
		assertEquals(actionLink.getRequestMethod(), "DELETE");
	}

	@Test
	public void testToRestWithKeys() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		SpiderDataRecord spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		spiderDataRecord.addKey("KEY1");

		DataRecordSpiderToRestConverter dataRecordSpiderToRestConverter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL);
		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();
		String key = restDataRecord.getKeys().iterator().next();
		assertEquals(key, "KEY1");

	}
}
