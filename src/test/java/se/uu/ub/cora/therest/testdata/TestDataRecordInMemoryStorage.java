package se.uu.ub.cora.therest.testdata;

import java.util.HashMap;
import java.util.Map;

import se.uu.ub.cora.metadataformat.data.DataAtomic;
import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.spider.record.storage.RecordStorageInMemory;

public class TestDataRecordInMemoryStorage {
	private static Map<String, Map<String, DataGroup>> records;

	public static RecordStorageInMemory createRecordStorageInMemoryWithTestData() {
		records = new HashMap<>();

		records.put("recordType", new HashMap<String, DataGroup>());
		addRecordTypeRecordType();
		addRecordTypePlace();
		addRecordTypeBadType();
		addAbstractRecordTypes();

		records.put("place", new HashMap<String, DataGroup>());

		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("type", "place"));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "place:0001"));

		/**
		 * <pre>
		 * 		recordInfo
		 * 			type
		 * 			id
		 * 			organisation
		 * 			user
		 * 			tsCreated (recordCreatedDate)
		 * 			list tsUpdated (recordUpdatedDate)
		 * 			catalog Language
		 * </pre>
		 */

		DataGroup dataGroup = DataGroup.withNameInData("authority");
		dataGroup.addChild(recordInfo);

		records.get("place").put("place:0001", dataGroup);

		RecordStorageInMemory recordsInMemory = new RecordStorageInMemory(records);
		return recordsInMemory;
	}

	private static void addRecordTypeRecordType() {
		String recordType = "recordType";
		DataGroup dataGroup = DataGroup.withNameInData(recordType);

		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "recordType"));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("type", recordType));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("id", "recordType"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("metadataId", "metadata:recordType"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("presentationViewId",
				"presentation:pgRecordTypeView"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("presentationFormId",
				"presentation:pgRecordTypeForm"));
		dataGroup
				.addChild(DataAtomic.withNameInDataAndValue("newMetadataId", "metadata:recordTypeNew"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("newPresentationFormId",
				"presentation:pgRecordTypeFormNew"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("listPresentationViewId",
				"presentation:pgRecordTypeViewList"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue("searchMetadataId", "metadata:recordTypeSearch"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("searchPresentationFormId",
				"presentation:pgRecordTypeSearchForm"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("userSuppliedId", "true"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("permissionKey", "RECORDTYPE_RECORDTYPE"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("selfPresentationViewId",
				"presentation:pgrecordTypeRecordType"));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("abstract", "false"));
		records.get(recordType).put("recordType", dataGroup);

	}

	private static void addRecordTypePlace() {
		String recordType = "recordType";
		DataGroup dataGroup = DataGroup.withNameInData(recordType);

		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "place"));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("type", recordType));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("id", "place"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("metadataId", "metadata:place"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue("presentationViewId", "presentation:pgPlaceView"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue("presentationFormId", "presentation:pgPlaceForm"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("newMetadataId", "metadata:placeNew"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("newPresentationFormId",
				"presentation:pgPlaceFormNew"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("listPresentationViewId",
				"presentation:pgPlaceViewList"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue("searchMetadataId", "metadata:placeSearch"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("searchPresentationFormId",
				"presentation:pgPlaceSearchForm"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("userSuppliedId", "false"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("permissionKey", "RECORDTYPE_PLACE"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("selfPresentationViewId",
				"presentation:pgPlaceRecordType"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("abstract", "false"));
		records.get(recordType).put("place", dataGroup);

	}

	private static void addRecordTypeBadType() {
		String recordType = "recordType";
		DataGroup dataGroup = DataGroup.withNameInData(recordType);

		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "place"));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("type", recordType));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("id", "place"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("metadataId", "metadata:place"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue("presentationViewId", "presentation:pgPlaceView"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue("presentationFormId", "presentation:pgPlaceForm"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("newMetadataId", "metadata:placeNew"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("newPresentationFormId",
				"presentation:pgPlaceFormNew"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("listPresentationViewId",
				"presentation:pgPlaceViewList"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue("searchMetadataId", "metadata:placeSearch"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("searchPresentationFormId",
				"presentation:pgPlaceSearchForm"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("userSuppliedId", "false"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("permissionKey", "RECORDTYPE_PLACE"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("selfPresentationViewId",
				"presentation:pgPlaceRecordType"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("abstract", "false"));
		records.get(recordType).put("place&& &&\\\\", dataGroup);
	}

	private static void addAbstractRecordTypes() {
		String recordType = "recordType";
		DataGroup dataGroup = DataGroup.withNameInData(recordType);

		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "abstract"));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("type", recordType));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("metadataId", "abstract"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue("presentationViewId", "presentation:pgAbstractView"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue("presentationFormId", "presentation:pgAbstractForm"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("newMetadataId", "metadata:abstractNew"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("newPresentationFormId",
				"presentation:pgAbstractFormNew"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("listPresentationViewId",
				"presentation:pgAbstractViewList"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue("searchMetadataId", "metadata:AbstractSearch"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("searchPresentationFormId",
				"presentation:pgAbstractSearchForm"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("userSuppliedId", "false"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("permissionKey", "RECORDTYPE_ABSTRACT"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("selfPresentationViewId",
				"presentation:pgAbstractRecordType"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("abstract", "true"));
		records.get(recordType).put("abstract", dataGroup);

		DataGroup dataGroup1 = DataGroup.withNameInData(recordType);

		DataGroup recordInfo1 = DataGroup.withNameInData("recordInfo1");
		recordInfo1.addChild(DataAtomic.withNameInDataAndValue("id", "child1"));
		recordInfo1.addChild(DataAtomic.withNameInDataAndValue("type", recordType));
		dataGroup1.addChild(recordInfo1);

		dataGroup1.addChild(DataAtomic.withNameInDataAndValue("metadataId", "child1"));
		dataGroup1.addChild(
				DataAtomic.withNameInDataAndValue("presentationViewId", "presentation:pgChild1View"));
		dataGroup1.addChild(
				DataAtomic.withNameInDataAndValue("presentationFormId", "presentation:pgChild1Form"));
		dataGroup1.addChild(DataAtomic.withNameInDataAndValue("newMetadataId", "metadata:child1New"));
		dataGroup1.addChild(DataAtomic.withNameInDataAndValue("newPresentationFormId",
				"presentation:pgChild1FormNew"));
		dataGroup1.addChild(DataAtomic.withNameInDataAndValue("listPresentationViewId",
				"presentation:pgChild1ViewList"));
		dataGroup1.addChild(
				DataAtomic.withNameInDataAndValue("searchMetadataId", "metadata:Child1Search"));
		dataGroup1.addChild(DataAtomic.withNameInDataAndValue("searchPresentationFormId",
				"presentation:pgChild1SearchForm"));
		dataGroup1.addChild(DataAtomic.withNameInDataAndValue("userSuppliedId", "true"));
		dataGroup1.addChild(DataAtomic.withNameInDataAndValue("permissionKey", "RECORDTYPE_CHILD1"));
		dataGroup1.addChild(DataAtomic.withNameInDataAndValue("selfPresentationViewId",
				"presentation:pgChild1RecordType"));

		dataGroup1.addChild(DataAtomic.withNameInDataAndValue("abstract", "false"));
		dataGroup1.addChild(DataAtomic.withNameInDataAndValue("parentId", "abstract"));
		records.get(recordType).put("child1", dataGroup1);

		DataGroup dataGroup2 = DataGroup.withNameInData(recordType);

		DataGroup recordInfo2 = DataGroup.withNameInData("recordInfo2");
		recordInfo2.addChild(DataAtomic.withNameInDataAndValue("id", "child2"));
		recordInfo2.addChild(DataAtomic.withNameInDataAndValue("type", recordType));
		dataGroup2.addChild(recordInfo2);

		dataGroup2.addChild(DataAtomic.withNameInDataAndValue("metadataId", "child2"));
		dataGroup2.addChild(
				DataAtomic.withNameInDataAndValue("presentationViewId", "presentation:pgChild2View"));
		dataGroup2.addChild(
				DataAtomic.withNameInDataAndValue("presentationFormId", "presentation:pgChild2Form"));
		dataGroup2.addChild(DataAtomic.withNameInDataAndValue("newMetadataId", "metadata:child2New"));
		dataGroup2.addChild(DataAtomic.withNameInDataAndValue("newPresentationFormId",
				"presentation:pgChild2FormNew"));
		dataGroup2.addChild(DataAtomic.withNameInDataAndValue("listPresentationViewId",
				"presentation:pgChild2ViewList"));
		dataGroup2.addChild(
				DataAtomic.withNameInDataAndValue("searchMetadataId", "metadata:Child2Search"));
		dataGroup2.addChild(DataAtomic.withNameInDataAndValue("searchPresentationFormId",
				"presentation:pgChild2SearchForm"));
		dataGroup2.addChild(DataAtomic.withNameInDataAndValue("userSuppliedId", "true"));
		dataGroup2.addChild(DataAtomic.withNameInDataAndValue("permissionKey", "RECORDTYPE_CHILD2"));
		dataGroup2.addChild(DataAtomic.withNameInDataAndValue("selfPresentationViewId",
				"presentation:pgChild2RecordType"));

		dataGroup2.addChild(DataAtomic.withNameInDataAndValue("abstract", "false"));
		dataGroup2.addChild(DataAtomic.withNameInDataAndValue("parentId", "abstract"));
		records.get(recordType).put("child2", dataGroup2);

	}
}
