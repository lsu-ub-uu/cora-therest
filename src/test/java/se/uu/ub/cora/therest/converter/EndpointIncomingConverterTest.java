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

import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.converter.ConverterProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.converter.JsonToDataConverterProvider;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.therest.record.ConverterFactorySpy;
import se.uu.ub.cora.therest.record.JsonParserSpy;
import se.uu.ub.cora.therest.record.JsonToDataConverterFactorySpy;
import se.uu.ub.cora.therest.record.JsonToDataConverterSpy;
import se.uu.ub.cora.therest.record.StringToExternallyConvertibleConverterSpy;

public class EndpointIncomingConverterTest {

	private EndpointIncomingConverter inConverter;
	private String jsonString = "{\"name\":\"someRecordType\",\"children\":[]}";
	private String xmlString = "<someXml></someXml>";
	private ConverterFactorySpy converterFactorySpy;
	private JsonToDataConverterFactorySpy jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();
	private StringToExternallyConvertibleConverterSpy stringToExternallyConvertibleConverterSpy;
	private JsonParserSpy jsonParser;

	@BeforeMethod
	public void beforeMethod() {
		LoggerFactorySpy loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);

		setupXmlConverter();
		setupJsonConverter();

		inConverter = new EndpointIncomingConverterImp(jsonParser);
	}

	private void setupXmlConverter() {
		stringToExternallyConvertibleConverterSpy = new StringToExternallyConvertibleConverterSpy();
		converterFactorySpy = new ConverterFactorySpy();
		converterFactorySpy.MRV.setDefaultReturnValuesSupplier(
				"factorStringToExternallyConvertableConverter",
				() -> stringToExternallyConvertibleConverterSpy);
		ConverterProvider.setConverterFactory("xml", converterFactorySpy);
	}

	private void setupJsonConverter() {
		jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();
		JsonToDataConverterProvider.setJsonToDataConverterFactory(jsonToDataConverterFactorySpy);
		jsonParser = new JsonParserSpy();
	}

	@Test
	public void testConvertXmlToElement() {
		DataGroup result = (DataGroup) inConverter.convertStringToConvertible(xmlString);

		DataGroup converted = assertConversionOfAndReturnSearchDataAsElementForXML();
		assertSame(result, converted);
	}

	private DataGroup assertConversionOfAndReturnSearchDataAsElementForXML() {
		var xmlToDataConverter = (StringToExternallyConvertibleConverterSpy) converterFactorySpy.MCR
				.getReturnValue("factorStringToExternallyConvertableConverter", 0);

		return (DataGroup) xmlToDataConverter.MCR.assertCalledParametersReturn("convert",
				xmlString);
	}

	@Test
	public void testConvertJsonToElement() {
		DataGroup result = (DataGroup) inConverter.convertStringToConvertible(jsonString);

		DataGroup converted = assertConversionOfAndReturnSearchDataAsElementForJSON();
		assertSame(result, converted);
	}

	private DataGroup assertConversionOfAndReturnSearchDataAsElementForJSON() {
		assertSame(jsonParser.jsonString, jsonString);
		assertSame(jsonToDataConverterFactorySpy.jsonValue, jsonParser.returnedJsonValue);
		JsonToDataConverterSpy jsonToDataConverterSpy = jsonToDataConverterFactorySpy.jsonToDataConverterSpy;
		return jsonToDataConverterSpy.dataPartToReturn;
	}

	@Test
	public void testOnlyForTestGetJsonParser() {
		JsonParser passedJsonParser = ((EndpointIncomingConverterImp) inConverter)
				.onlyForTestGetJsonParser();
		assertSame(jsonParser, passedJsonParser);
	}

}
