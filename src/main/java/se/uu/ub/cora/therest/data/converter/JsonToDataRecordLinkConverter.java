/*
 * Copyright 2015 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.therest.data.converter;

import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataRecordLink;
import se.uu.ub.cora.therest.json.parser.JsonArray;
import se.uu.ub.cora.therest.json.parser.JsonObject;
import se.uu.ub.cora.therest.json.parser.JsonParseException;
import se.uu.ub.cora.therest.json.parser.JsonValue;

public final class JsonToDataRecordLinkConverter implements JsonToDataConverter {

	private static final int NUM_OF_MANDATORY_KEYS = 2;
	private static final String LINKED_RECORD_ID = "linkedRecordId";
	private static final String LINKED_REPEAT_ID = "linkedRepeatId";
	private JsonObject jsonObject;
	private static final int NUM_OF_ALLOWED_KEYS_AT_TOP_LEVEL = 4;

	public static JsonToDataRecordLinkConverter forJsonObject(JsonObject jsonObject) {
		return new JsonToDataRecordLinkConverter(jsonObject);
	}

	private JsonToDataRecordLinkConverter(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	@Override
	public RestDataElement toInstance() {
		try {
			return tryToInstantiate();
		} catch (Exception e) {
			throw new JsonParseException("Error parsing jsonObject: " + e.getMessage(), e);
		}
	}

	private RestDataElement tryToInstantiate() {
		validateOnlyCorrectKeysAtTopLevel();
		return createDataGroupInstance();
	}

	private void validateOnlyCorrectKeysAtTopLevel() {
		validateMandatoryKeysAndMandatoryChild();

		if (moreKeysAtTopLevelThanAllowed()
				|| moreKeysAtTopLevelThanMandatoryButEnoughOptionalKeysNotFound()
				|| moreChildrenThanMandatoryButEnoughOptionalChildrenNotFound()) {
			throw new JsonParseException("Group data can only contain keys name, repeatId, "
					+ "linkedRecordId, linkedRepeatId and actionLinks");
		}
 	}

	private void validateMandatoryKeysAndMandatoryChild() {
		if (!jsonObject.containsKey("name")) {
			throw new JsonParseException("Group data must contain key \"name\"");
		}
		if(!jsonObject.containsKey("children")){
			throw new JsonParseException("Group data must contain key \"children\"");
		}
		if (!hasChild(LINKED_RECORD_ID)) {
			throw new JsonParseException("Group data must contain key \"linkedRecordId\"");
		}
	}

	private boolean hasChild(String childName){
		JsonObject child = getChildObjectFromJsonObject(childName);
		if(child != null){
			return true;
		}
//		JsonArray children = jsonObject.getValueAsJsonArray("children");
//		for (JsonValue child : children) {
//			if(isSameChild(childName, child)){
//				return true;
//			}
//		}
		return  false;
	}

	private boolean isSameChild(String childName, JsonValue child) {
		JsonObject childObject =  (JsonObject)child;
		return childObject.containsKey("name") && nameIsSameAsChildName(childObject, childName);
	}
	
	//TODO:ska vi kolla om det är ett atomärt värde? För tydligare felmeddelande?
	private boolean nameIsSameAsChildName(JsonObject childObject, String childName) {
		String name = childObject.getValueAsJsonString("name").getStringValue();
		return name.equals(childName);
	}

	private boolean moreKeysAtTopLevelThanMandatoryButEnoughOptionalKeysNotFound() {
		int numOfOptionalKeysPresent = countNumOfPresentOptionalKeys();

		int keySize = jsonObject.keySet().size();
		int totalNumOfCorrectKeys = numOfOptionalKeysPresent + NUM_OF_MANDATORY_KEYS;

		return keySize != totalNumOfCorrectKeys;
	}

	private int countNumOfPresentOptionalKeys() {
		int numOfOptionalKeysPresent = 0;
		if(hasActionLinks()){
			numOfOptionalKeysPresent++;
		}
		if(hasRepeatId()){
			numOfOptionalKeysPresent++;
		}
		return numOfOptionalKeysPresent;
	}

	private boolean hasActionLinks() {
		return jsonObject.containsKey("actionLinks");
	}
	
	private boolean hasRepeatId() {
		return jsonObject.containsKey("repeatId");
	}

	private boolean moreChildrenThanMandatoryButEnoughOptionalChildrenNotFound(){
		JsonArray children = jsonObject.getValueAsJsonArray("children");
		if(children.length() > 1 && !hasChild(LINKED_REPEAT_ID)){
			return true;
		}
		return false;
	}

	private boolean moreKeysAtTopLevelThanAllowed() {
		return jsonObject.keySet().size() > NUM_OF_ALLOWED_KEYS_AT_TOP_LEVEL;
	}

	private RestDataElement createDataGroupInstance() {
		String nameInData = getStringValueFromJsonObject("name");
		RestDataRecordLink restDataRecordLink = RestDataRecordLink.withNameInData(nameInData);

		RestDataAtomic linkedRecordIdElement = createLinkedRecordId();
		restDataRecordLink.addChild(linkedRecordIdElement);
		
		possilySetRepeatId(restDataRecordLink);
		possiblyAddLinkedRepeatId(restDataRecordLink);
		
		return restDataRecordLink;
	}

	private String getStringValueFromJsonObject(String id) {
		return jsonObject.getValueAsJsonString(id).getStringValue();
	}
	
	private String getStringValueFromJsonObjectChildren(String id){
		JsonObject childObject = getChildObjectFromJsonObject(id);
		return childObject.getValueAsJsonString("value").getStringValue();
	}
	//TODO: hur göra med den här och den andra loopen som är nästan lika?
	private JsonObject getChildObjectFromJsonObject(String id) {
		JsonArray children = jsonObject.getValueAsJsonArray("children");
		for (JsonValue child : children) {
			if(isSameChild(id, child)){
				return (JsonObject)child;
			}
		}
		return  null;
	}
	
	private RestDataAtomic createLinkedRecordId() {
		String linkedRecordId = getStringValueFromJsonObjectChildren(LINKED_RECORD_ID);
		RestDataAtomic linkedRecordIdElement = RestDataAtomic.withNameInDataAndValue(LINKED_RECORD_ID, linkedRecordId);
		return linkedRecordIdElement;
	}
	
	private void possilySetRepeatId(RestDataRecordLink restDataRecordLink) {
		if (hasRepeatId()) {
			restDataRecordLink.setRepeatId(getStringValueFromJsonObject("repeatId"));
		}
	}
	
	private void possiblyAddLinkedRepeatId(RestDataRecordLink restDataRecordLink) {
		if(hasLinkedRepeatId()) {
			RestDataAtomic linkedRepeatId = RestDataAtomic.withNameInDataAndValue(LINKED_REPEAT_ID,
					getStringValueFromJsonObjectChildren(LINKED_REPEAT_ID));
			restDataRecordLink.addChild(linkedRepeatId);
		}
	}

	private boolean hasLinkedRepeatId() {
		return hasChild(LINKED_REPEAT_ID);
	}
}
