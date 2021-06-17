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

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataGroup;

public class CoraDataGroupToRestConverterFactorySpy implements CoraDataGroupToRestConverterFactory {

	public List<DataGroup> dataGroups = new ArrayList<>();
	public List<Action> addedActions = new ArrayList<>();
	public List<ConverterInfo> converterInfos = new ArrayList<>();
	public List<CoraToRestConverterSpy> factoredSpiderToRestConverters = new ArrayList<>();
	public List<CoraActionToRestConverterSpy> factoredActionsToRestConverters = new ArrayList<>();

	@Override
	public CoraToRestConverter factorForDataGroupWithConverterInfo(DataGroup dataGroup,
			ConverterInfo converterInfo) {
		dataGroups.add(dataGroup);
		converterInfos.add(converterInfo);
		CoraToRestConverterSpy converter = new CoraToRestConverterSpy();
		factoredSpiderToRestConverters.add(converter);

		return converter;
	}

	@Override
	public CoraActionToRestConverter factorForActionsUsingConverterInfoAndDataGroup(
			List<Action> actions, ConverterInfo converterInfo, DataGroup dataGroup) {
		addedActions.addAll(actions);
		dataGroups.add(dataGroup);
		converterInfos.add(converterInfo);
		CoraActionToRestConverterSpy converter = new CoraActionToRestConverterSpy();
		factoredActionsToRestConverters.add(converter);
		return converter;
	}

}
