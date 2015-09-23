package epc.therest.testdata;

import java.util.HashMap;
import java.util.Map;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;
import epc.spider.record.storage.RecordStorageInMemory;

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

		DataGroup recordInfo = DataGroup.withDataId("recordInfo");
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", "place"));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("id", "place:0001"));

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

		DataGroup dataGroup = DataGroup.withDataId("authority");
		dataGroup.addChild(recordInfo);

		records.get("place").put("place:0001", dataGroup);

		RecordStorageInMemory recordsInMemory = new RecordStorageInMemory(records);
		return recordsInMemory;
	}

	private static void addRecordTypeRecordType() {
		String recordType = "recordType";
		DataGroup dataGroup = DataGroup.withDataId(recordType);

		DataGroup recordInfo = DataGroup.withDataId("recordInfo");
		recordInfo.addChild(DataAtomic.withDataIdAndValue("id", "recordType"));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", recordType));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withDataIdAndValue("id", "recordType"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("metadataId", "metadata:recordType"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("presentationViewId",
				"presentation:pgRecordTypeView"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("presentationFormId",
				"presentation:pgRecordTypeForm"));
		dataGroup
				.addChild(DataAtomic.withDataIdAndValue("newMetadataId", "metadata:recordTypeNew"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("newPresentationFormId",
				"presentation:pgRecordTypeFormNew"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("listPresentationViewId",
				"presentation:pgRecordTypeViewList"));
		dataGroup.addChild(
				DataAtomic.withDataIdAndValue("searchMetadataId", "metadata:recordTypeSearch"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("searchPresentationFormId",
				"presentation:pgRecordTypeSearchForm"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("userSuppliedId", "true"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("permissionKey", "RECORDTYPE_RECORDTYPE"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("selfPresentationViewId",
				"presentation:pgrecordTypeRecordType"));

		dataGroup.addChild(DataAtomic.withDataIdAndValue("abstract", "false"));
		records.get(recordType).put("recordType", dataGroup);

	}

	private static void addRecordTypePlace() {
		String recordType = "recordType";
		DataGroup dataGroup = DataGroup.withDataId(recordType);

		DataGroup recordInfo = DataGroup.withDataId("recordInfo");
		recordInfo.addChild(DataAtomic.withDataIdAndValue("id", "place"));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", recordType));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withDataIdAndValue("id", "place"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("metadataId", "metadata:place"));
		dataGroup.addChild(
				DataAtomic.withDataIdAndValue("presentationViewId", "presentation:pgPlaceView"));
		dataGroup.addChild(
				DataAtomic.withDataIdAndValue("presentationFormId", "presentation:pgPlaceForm"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("newMetadataId", "metadata:placeNew"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("newPresentationFormId",
				"presentation:pgPlaceFormNew"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("listPresentationViewId",
				"presentation:pgPlaceViewList"));
		dataGroup.addChild(
				DataAtomic.withDataIdAndValue("searchMetadataId", "metadata:placeSearch"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("searchPresentationFormId",
				"presentation:pgPlaceSearchForm"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("userSuppliedId", "false"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("permissionKey", "RECORDTYPE_PLACE"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("selfPresentationViewId",
				"presentation:pgPlaceRecordType"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("abstract", "false"));
		records.get(recordType).put("place", dataGroup);

	}

	private static void addRecordTypeBadType() {
		String recordType = "recordType";
		DataGroup dataGroup = DataGroup.withDataId(recordType);

		DataGroup recordInfo = DataGroup.withDataId("recordInfo");
		recordInfo.addChild(DataAtomic.withDataIdAndValue("id", "place"));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", recordType));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withDataIdAndValue("id", "place"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("metadataId", "metadata:place"));
		dataGroup.addChild(
				DataAtomic.withDataIdAndValue("presentationViewId", "presentation:pgPlaceView"));
		dataGroup.addChild(
				DataAtomic.withDataIdAndValue("presentationFormId", "presentation:pgPlaceForm"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("newMetadataId", "metadata:placeNew"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("newPresentationFormId",
				"presentation:pgPlaceFormNew"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("listPresentationViewId",
				"presentation:pgPlaceViewList"));
		dataGroup.addChild(
				DataAtomic.withDataIdAndValue("searchMetadataId", "metadata:placeSearch"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("searchPresentationFormId",
				"presentation:pgPlaceSearchForm"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("userSuppliedId", "false"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("permissionKey", "RECORDTYPE_PLACE"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("selfPresentationViewId",
				"presentation:pgPlaceRecordType"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("abstract", "false"));
		records.get(recordType).put("place&& &&\\\\", dataGroup);
	}

	private static void addAbstractRecordTypes() {
		String recordType = "recordType";
		DataGroup dataGroup = DataGroup.withDataId(recordType);

		DataGroup recordInfo = DataGroup.withDataId("recordInfo");
		recordInfo.addChild(DataAtomic.withDataIdAndValue("id", "abstract"));
		recordInfo.addChild(DataAtomic.withDataIdAndValue("type", recordType));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withDataIdAndValue("metadataId", "abstract"));
		dataGroup.addChild(
				DataAtomic.withDataIdAndValue("presentationViewId", "presentation:pgAbstractView"));
		dataGroup.addChild(
				DataAtomic.withDataIdAndValue("presentationFormId", "presentation:pgAbstractForm"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("newMetadataId", "metadata:abstractNew"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("newPresentationFormId",
				"presentation:pgAbstractFormNew"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("listPresentationViewId",
				"presentation:pgAbstractViewList"));
		dataGroup.addChild(
				DataAtomic.withDataIdAndValue("searchMetadataId", "metadata:AbstractSearch"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("searchPresentationFormId",
				"presentation:pgAbstractSearchForm"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("userSuppliedId", "false"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("permissionKey", "RECORDTYPE_ABSTRACT"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("selfPresentationViewId",
				"presentation:pgAbstractRecordType"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("abstract", "true"));
		records.get(recordType).put("abstract", dataGroup);

		DataGroup dataGroup1 = DataGroup.withDataId(recordType);

		DataGroup recordInfo1 = DataGroup.withDataId("recordInfo1");
		recordInfo1.addChild(DataAtomic.withDataIdAndValue("id", "child1"));
		recordInfo1.addChild(DataAtomic.withDataIdAndValue("type", recordType));
		dataGroup1.addChild(recordInfo1);

		dataGroup1.addChild(DataAtomic.withDataIdAndValue("metadataId", "child1"));
		dataGroup1.addChild(
				DataAtomic.withDataIdAndValue("presentationViewId", "presentation:pgChild1View"));
		dataGroup1.addChild(
				DataAtomic.withDataIdAndValue("presentationFormId", "presentation:pgChild1Form"));
		dataGroup1.addChild(DataAtomic.withDataIdAndValue("newMetadataId", "metadata:child1New"));
		dataGroup1.addChild(DataAtomic.withDataIdAndValue("newPresentationFormId",
				"presentation:pgChild1FormNew"));
		dataGroup1.addChild(DataAtomic.withDataIdAndValue("listPresentationViewId",
				"presentation:pgChild1ViewList"));
		dataGroup1.addChild(
				DataAtomic.withDataIdAndValue("searchMetadataId", "metadata:Child1Search"));
		dataGroup1.addChild(DataAtomic.withDataIdAndValue("searchPresentationFormId",
				"presentation:pgChild1SearchForm"));
		dataGroup1.addChild(DataAtomic.withDataIdAndValue("userSuppliedId", "true"));
		dataGroup1.addChild(DataAtomic.withDataIdAndValue("permissionKey", "RECORDTYPE_CHILD1"));
		dataGroup1.addChild(DataAtomic.withDataIdAndValue("selfPresentationViewId",
				"presentation:pgChild1RecordType"));

		dataGroup1.addChild(DataAtomic.withDataIdAndValue("abstract", "false"));
		dataGroup1.addChild(DataAtomic.withDataIdAndValue("parentId", "abstract"));
		records.get(recordType).put("child1", dataGroup1);

		DataGroup dataGroup2 = DataGroup.withDataId(recordType);

		DataGroup recordInfo2 = DataGroup.withDataId("recordInfo2");
		recordInfo2.addChild(DataAtomic.withDataIdAndValue("id", "child2"));
		recordInfo2.addChild(DataAtomic.withDataIdAndValue("type", recordType));
		dataGroup2.addChild(recordInfo2);

		dataGroup2.addChild(DataAtomic.withDataIdAndValue("metadataId", "child2"));
		dataGroup2.addChild(
				DataAtomic.withDataIdAndValue("presentationViewId", "presentation:pgChild2View"));
		dataGroup2.addChild(
				DataAtomic.withDataIdAndValue("presentationFormId", "presentation:pgChild2Form"));
		dataGroup2.addChild(DataAtomic.withDataIdAndValue("newMetadataId", "metadata:child2New"));
		dataGroup2.addChild(DataAtomic.withDataIdAndValue("newPresentationFormId",
				"presentation:pgChild2FormNew"));
		dataGroup2.addChild(DataAtomic.withDataIdAndValue("listPresentationViewId",
				"presentation:pgChild2ViewList"));
		dataGroup2.addChild(
				DataAtomic.withDataIdAndValue("searchMetadataId", "metadata:Child2Search"));
		dataGroup2.addChild(DataAtomic.withDataIdAndValue("searchPresentationFormId",
				"presentation:pgChild2SearchForm"));
		dataGroup2.addChild(DataAtomic.withDataIdAndValue("userSuppliedId", "true"));
		dataGroup2.addChild(DataAtomic.withDataIdAndValue("permissionKey", "RECORDTYPE_CHILD2"));
		dataGroup2.addChild(DataAtomic.withDataIdAndValue("selfPresentationViewId",
				"presentation:pgChild2RecordType"));

		dataGroup2.addChild(DataAtomic.withDataIdAndValue("abstract", "false"));
		dataGroup2.addChild(DataAtomic.withDataIdAndValue("parentId", "abstract"));
		records.get(recordType).put("child2", dataGroup2);

	}
}
