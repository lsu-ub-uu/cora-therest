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
package se.uu.ub.cora.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.Action;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.therest.data.converter.ConverterInfo;

public class SpiderToRestConverterFactoryTest {

	@Test
	public void testFactorForDataGroup() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("someDataGroup");
		ConverterInfo converterInfo = ConverterInfo.withBaseURLAndRecordURLAndTypeAndId(
				"someBaseUrl", "someRecordUrl", "someRecordType", "someRecordId");
		SpiderToRestConverterFactory factory = new SpiderToRestConverterFactoryImp();
		DataGroupSpiderToRestConverter converter = (DataGroupSpiderToRestConverter) factory
				.factorForSpiderDataGroupWithConverterInfo(spiderDataGroup, converterInfo);

		assertEquals(converter.convertInfo.baseURL, "someBaseUrl");
		assertSame(converter.spiderDataGroup, spiderDataGroup);
	}

	@Test
	public void testFactorForActions() {
		List<Action> actions = new ArrayList<>();
		Action action = Action.READ;
		actions.add(action);

		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("someDataGroup");
		ConverterInfo converterInfo = ConverterInfo.withBaseURLAndRecordURLAndTypeAndId(
				"someBaseUrl", "someRecordUrl", "someRecordType", "someRecordId");
		SpiderToRestConverterFactory factory = new SpiderToRestConverterFactoryImp();
		ActionSpiderToRestConverterImp converter = (ActionSpiderToRestConverterImp) factory
				.factorForActionsUsingConverterInfoAndDataGroup(actions, converterInfo,
						spiderDataGroup);

		assertSame(converter.getDataGroup(), spiderDataGroup);
		assertSame(converter.getConverterInfo(), converterInfo);
		assertSame(converter.getActions(), actions);
	}

}
