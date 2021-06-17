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
package se.uu.ub.cora.therest.coradata;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataResourceLink;

public class DataResourceLinkSpy extends DataGroupSpy implements DataResourceLink {

	public String nameInData;
	public List<Action> actions = new ArrayList<>();

	public DataResourceLinkSpy(String nameInData) {
		super(nameInData);
	}

	@Override
	public void addAction(Action action) {
		actions.add(action);

	}

	@Override
	public List<Action> getActions() {
		return actions;
	}

}
