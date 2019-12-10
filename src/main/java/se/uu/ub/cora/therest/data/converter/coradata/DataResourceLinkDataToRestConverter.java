/*
 * Copyright 2015, 2016, 2019 Uppsala University Library
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataResourceLink;
import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataResourceLink;
import se.uu.ub.cora.therest.data.converter.ConverterInfo;

public final class DataResourceLinkDataToRestConverter extends DataGroupDataToRestConverter {
	private DataResourceLink dataResourceLink;
	private RestDataResourceLink restDataResourceLink;

	public static DataResourceLinkDataToRestConverter fromDataResourceLinkWithConverterInfo(
			DataResourceLink dataResourceLink, ConverterInfo converterInfo) {
		return new DataResourceLinkDataToRestConverter(dataResourceLink, converterInfo);
	}

	private DataResourceLinkDataToRestConverter(DataResourceLink dataResourceLink,
			ConverterInfo converterInfo) {
		super(dataResourceLink, converterInfo);
		this.dataResourceLink = dataResourceLink;
	}

	@Override
	protected RestDataGroup createNewRest() {
		return RestDataResourceLink.withNameInData(dataGroup.getNameInData());
	}

	@Override
	public RestDataResourceLink toRest() {
		restDataResourceLink = (RestDataResourceLink) super.toRest();

		createRestLinks();
		return restDataResourceLink;
	}

	private void createRestLinks() {
		String url = convertInfo.recordURL + "/" + dataResourceLink.getNameInData();
		String mimeType = dataResourceLink.getFirstAtomicValueWithNameInData("mimeType");
		List<Action> actions = dataResourceLink.getActions();

		Map<String, ActionLink> actionLinks = new LinkedHashMap<>(actions.size());
		for (Action action : actions) {
			createRestLink(url, mimeType, actionLinks, action);
		}
		restDataResourceLink.setActionLinks(actionLinks);
	}

	private void createRestLink(String url, String mimeType, Map<String, ActionLink> actionLinks,
			Action action) {
		ActionLink actionLink = ActionLink.withAction(action);
		actionLink.setRequestMethod("GET");
		actionLink.setURL(url);
		actionLink.setAccept(mimeType);

		actionLinks.put("read", actionLink);
	}
}
