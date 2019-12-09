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

package se.uu.ub.cora.therest.data.converter.spider;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.therest.data.RestDataAtomic;

public final class DataAtomicRestToDataConverter {

	public static DataAtomicRestToDataConverter fromRestDataAtomic(
			RestDataAtomic restDataAtomic) {
		return new DataAtomicRestToDataConverter(restDataAtomic);
	}

	private RestDataAtomic restDataAtomic;

	private DataAtomicRestToDataConverter(RestDataAtomic restDataAtomic) {
		this.restDataAtomic = restDataAtomic;
	}

	public DataAtomic convert() {
		DataAtomic spiderDataAtomic = DataAtomicProvider.getDataAtomicUsingNameInDataAndValue(
				restDataAtomic.getNameInData(), restDataAtomic.getValue());
		spiderDataAtomic.setRepeatId(restDataAtomic.getRepeatId());
		return spiderDataAtomic;
	}

}
