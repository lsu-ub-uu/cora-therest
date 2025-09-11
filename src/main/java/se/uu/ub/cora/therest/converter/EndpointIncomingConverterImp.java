/*
 * Copyright 2025 Uppsala University Library
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
package se.uu.ub.cora.therest.converter;

import se.uu.ub.cora.converter.ConverterProvider;
import se.uu.ub.cora.converter.StringToExternallyConvertibleConverter;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.ExternallyConvertible;
import se.uu.ub.cora.data.converter.JsonToDataConverter;
import se.uu.ub.cora.data.converter.JsonToDataConverterProvider;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;

public class EndpointIncomingConverterImp implements EndpointIncomingConverter {
	private JsonParser jsonParser;

	public EndpointIncomingConverterImp(JsonParser jsonParser) {
		this.jsonParser = jsonParser;
	}

	@Override
	public ExternallyConvertible convertStringToConvertible(String data) {
		return convertSearchStringToData(data);
	}

	private DataGroup convertSearchStringToData(String searchDataAsString) {
		String searchDataType = calculateSearchDataType(searchDataAsString);
		return convertStringToDataGroup(searchDataType, searchDataAsString);
	}

	private String calculateSearchDataType(String searchDataAsString) {
		if (searchDataAsString.startsWith("<")) {
			return "+xml";
		}
		return "+json";
	}

	private DataGroup convertStringToDataGroup(String accept, String input) {
		if (accept.endsWith("+xml")) {
			return convertXmlToDataElement(input);
		} else {
			return convertJsonStringToDataGroup(input);
		}
	}

	private DataGroup convertXmlToDataElement(String input) {
		StringToExternallyConvertibleConverter xmlToConvertibleConverter = ConverterProvider
				.getStringToExternallyConvertibleConverter("xml");
		return (DataGroup) xmlToConvertibleConverter.convert(input);
	}

	private DataGroup convertJsonStringToDataGroup(String jsonRecord) {
		JsonValue jsonValue = jsonParser.parseString(jsonRecord);
		JsonToDataConverter jsonToDataConverter = JsonToDataConverterProvider
				.getConverterUsingJsonObject(jsonValue);
		return (DataGroup) jsonToDataConverter.toInstance();
	}

	public JsonParser onlyForTestGetJsonParser() {
		return jsonParser;
	}
}
