/*
 * Copyright 2019 Uppsala University Library
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
package se.uu.ub.cora.therest.converter.coratorest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.therest.coradata.DataGroupSpy;

public class CoraDataGroupToRestConverterFactoryTest {

	@Test
	public void testFactorForDataGroup() {
		DataGroup dataGroup = new DataGroupSpy("someDataGroup");
		ConverterInfo converterInfo = ConverterInfo.withBaseURLAndRecordURLAndTypeAndId(
				"someBaseUrl", "someRecordUrl", "someRecordType", "someRecordId");
		CoraDataGroupToRestConverterFactory factory = new CoraDataGroupToRestConverterFactoryImp();
		CoraDataGroupToRestConverter converter = (CoraDataGroupToRestConverter) factory
				.factorForDataGroupWithConverterInfo(dataGroup, converterInfo);

		assertEquals(converter.convertInfo.baseURL, "someBaseUrl");
		assertSame(converter.dataGroup, dataGroup);
	}

	@Test
	public void testFactorForActions() {
		List<Action> actions = new ArrayList<>();
		Action action = Action.READ;
		actions.add(action);

		DataGroup dataGroup = new DataGroupSpy("someDataGroup");
		ConverterInfo converterInfo = ConverterInfo.withBaseURLAndRecordURLAndTypeAndId(
				"someBaseUrl", "someRecordUrl", "someRecordType", "someRecordId");
		CoraDataGroupToRestConverterFactory factory = new CoraDataGroupToRestConverterFactoryImp();
		CoraActionToRestConverterImp converter = (CoraActionToRestConverterImp) factory
				.factorForActionsUsingConverterInfoAndDataGroup(actions, converterInfo, dataGroup);

		assertSame(converter.getDataGroup(), dataGroup);
		assertSame(converter.getConverterInfo(), converterInfo);
		assertSame(converter.getActions(), actions);
	}

}
