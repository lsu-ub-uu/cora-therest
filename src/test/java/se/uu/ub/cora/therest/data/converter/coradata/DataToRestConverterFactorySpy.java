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
package se.uu.ub.cora.therest.data.converter.coradata;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.therest.data.converter.ConverterInfo;
import se.uu.ub.cora.therest.data.converter.coradata.ActionDataToRestConverter;
import se.uu.ub.cora.therest.data.converter.coradata.DataToRestConverter;
import se.uu.ub.cora.therest.data.converter.coradata.DataToRestConverterFactory;

public class DataToRestConverterFactorySpy implements DataToRestConverterFactory {

	public List<DataGroup> dataGroups = new ArrayList<>();
	public List<Action> addedActions = new ArrayList<>();
	public List<ConverterInfo> converterInfos = new ArrayList<>();
	public List<DataToRestConverterSpy> factoredSpiderToRestConverters = new ArrayList<>();
	public List<ActionDataToRestConverterSpy> factoredActionsToRestConverters = new ArrayList<>();

	@Override
	public DataToRestConverter factorForDataGroupWithConverterInfo(
			DataGroup spiderDataGroup, ConverterInfo converterInfo) {
		dataGroups.add(spiderDataGroup);
		converterInfos.add(converterInfo);
		DataToRestConverterSpy converter = new DataToRestConverterSpy();
		factoredSpiderToRestConverters.add(converter);

		return converter;
	}

	@Override
	public ActionDataToRestConverter factorForActionsUsingConverterInfoAndDataGroup(
			List<Action> actions, ConverterInfo converterInfo, DataGroup spiderDataGroup) {
		addedActions.addAll(actions);
		dataGroups.add(spiderDataGroup);
		converterInfos.add(converterInfo);
		ActionDataToRestConverterSpy converter = new ActionDataToRestConverterSpy();
		factoredActionsToRestConverters.add(converter);
		return converter;
	}

}
