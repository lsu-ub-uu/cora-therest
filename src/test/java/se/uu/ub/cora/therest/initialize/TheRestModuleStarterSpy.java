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
package se.uu.ub.cora.therest.initialize;

import java.util.Map;

import se.uu.ub.cora.storage.MetadataStorageProvider;
import se.uu.ub.cora.storage.RecordIdGeneratorProvider;
import se.uu.ub.cora.storage.RecordStorageProvider;
import se.uu.ub.cora.storage.StreamStorageProvider;

public class TheRestModuleStarterSpy implements TheRestModuleStarter {

	boolean startWasCalled = false;
	Map<String, String> initInfo;
	public Iterable<RecordStorageProvider> recordStorageProviderImplementations;
	public Iterable<StreamStorageProvider> streamStorageProviderImplementations;
	public Iterable<RecordIdGeneratorProvider> recordIdGeneratorProviderImplementations;
	public Iterable<MetadataStorageProvider> metadataStorageProviderImplementations;

	@Override
	public void startUsingInitInfoAndProviders(Map<String, String> initInfo, Providers providers) {
		recordStorageProviderImplementations = providers.recordStorageProviderImplementations;
		streamStorageProviderImplementations = providers.streamStorageProviderImplementations;
		recordIdGeneratorProviderImplementations = providers.recordIdGeneratorProviderImplementations;
		metadataStorageProviderImplementations = providers.metadataStorageProviderImplementations;
		this.initInfo = initInfo;
		startWasCalled = true;

	}

}
