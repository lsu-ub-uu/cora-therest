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

package se.uu.ub.cora.therest.record;

import se.uu.ub.cora.bookkeeper.validator.DataValidator;
import se.uu.ub.cora.bookkeeper.validator.ValidationAnswer;
import se.uu.ub.cora.data.DataGroup;

public class DataValidatorAlwaysValidSpy implements DataValidator {
	boolean validateDataWasCalled = false;

	@Override
	public ValidationAnswer validateData(String metadataGroupId, DataGroup dataGroup) {
		validateDataWasCalled = true;
		return new ValidationAnswer();
	}

	@Override
	public ValidationAnswer validateListFilter(String recordType, DataGroup filterDataGroup) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValidationAnswer validateIndexSettings(String recordType, DataGroup indexSettings) {
		// TODO Auto-generated method stub
		return null;
	}

}
