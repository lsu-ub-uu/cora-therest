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

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.therest.data.RestDataAtomic;

public class DataAtomicToJsonConverterTest {
	private RestDataAtomic restDataAtomic;
	private DataToJsonConverter converter;

	@BeforeMethod
	public void beforeMethod() {
		restDataAtomic = RestDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue");
		OrgJsonBuilderFactoryAdapter factory = new OrgJsonBuilderFactoryAdapter();
		converter = DataAtomicToJsonConverter.usingJsonFactoryForRestDataAtomic(factory,
				restDataAtomic);
	}

	@Test
	public void testToJson() {
		String json = converter.toJson();

		Assert.assertEquals(json, "{\"name\":\"atomicNameInData\",\"value\":\"atomicValue\"}");
	}

	@Test
	public void testToJsonWithRepeatId() {
		restDataAtomic.setRepeatId("2");
		String json = converter.toJson();

		Assert.assertEquals(json,
				"{\"repeatId\":\"2\",\"name\":\"atomicNameInData\",\"value\":\"atomicValue\"}");
	}

	@Test
	public void testToJsonWithEmptyRepeatId() {
		restDataAtomic.setRepeatId("");
		String json = converter.toJson();

		Assert.assertEquals(json, "{\"name\":\"atomicNameInData\",\"value\":\"atomicValue\"}");
	}

	@Test
	public void testToJsonEmptyValue() {
		RestDataAtomic restDataAtomic = RestDataAtomic.withNameInDataAndValue("atomicNameInData",
				"");
		OrgJsonBuilderFactoryAdapter factory = new OrgJsonBuilderFactoryAdapter();
		converter = DataAtomicToJsonConverter.usingJsonFactoryForRestDataAtomic(factory,
				restDataAtomic);
		String json = converter.toJson();

		Assert.assertEquals(json, "{\"name\":\"atomicNameInData\",\"value\":\"\"}");
	}
}
