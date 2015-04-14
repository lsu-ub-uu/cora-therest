package epc.therest.data.converter.spider;

import epc.spider.data.Action;
import epc.spider.data.SpiderDataAtomic;
import epc.spider.data.SpiderDataElement;
import epc.spider.data.SpiderDataGroup;
import epc.therest.data.ActionLink;
import epc.therest.data.RestDataElement;
import epc.therest.data.RestDataGroup;
import epc.therest.data.converter.ConverterException;

public final class DataGroupSpiderToRestConverter {

	private static String baseURL;

	public static DataGroupSpiderToRestConverter fromSpiderDataGroupWithBaseURL(SpiderDataGroup spiderDataGroup,
			String baseURL) {
		DataGroupSpiderToRestConverter.baseURL = baseURL;
		return new DataGroupSpiderToRestConverter(spiderDataGroup);
	}

	private SpiderDataGroup spiderDataGroup;
	private RestDataGroup restDataGroup;

	private DataGroupSpiderToRestConverter(SpiderDataGroup spiderDataGroup) {
		this.spiderDataGroup = spiderDataGroup;
	}

	public RestDataGroup toRest() {
		restDataGroup = RestDataGroup.withDataId(spiderDataGroup.getDataId());
		restDataGroup.getAttributes().putAll(spiderDataGroup.getAttributes());
		convertAndSetChildren();
		if (hasActions()) {
			convertActionsToLinks();
		}
		return restDataGroup;
	}

	private boolean hasActions() {
		return !spiderDataGroup.getActions().isEmpty();
	}

	private void convertAndSetChildren() {
		for (SpiderDataElement spiderDataElement : spiderDataGroup.getChildren()) {
			RestDataElement convertedChild = convertToElementEquivalentDataClass(spiderDataElement);
			restDataGroup.getChildren().add(convertedChild);
		}
	}

	private RestDataElement convertToElementEquivalentDataClass(SpiderDataElement spiderDataElement) {
		if (spiderDataElement instanceof SpiderDataGroup) {
			return DataGroupSpiderToRestConverter.fromSpiderDataGroupWithBaseURL((SpiderDataGroup) spiderDataElement,
					baseURL).toRest();
		}
		return DataAtomicSpiderToRestConverter.fromSpiderDataAtomic(
				(SpiderDataAtomic) spiderDataElement).toRest();
	}

	private void convertActionsToLinks() {
		SpiderDataGroup recordInfo = findRecordInfo();
		String id = findId(recordInfo);
		String type = findType(recordInfo);
		createRestLinks(id, type);
	}

	private SpiderDataGroup findRecordInfo() {
		SpiderDataGroup recordInfo = null;
		for (SpiderDataElement spiderDataElement : spiderDataGroup.getChildren()) {
			if ("recordInfo".equals(spiderDataElement.getDataId())) {
				recordInfo = (SpiderDataGroup) spiderDataElement;
				break;
			}
		}
		if (null == recordInfo) {
			throw new ConverterException("No recordInfo found convertion not possible");
		}
		return recordInfo;
	}

	private String findId(SpiderDataGroup recordInfo) {
		String id = "";
		for (SpiderDataElement spiderDataElement : recordInfo.getChildren()) {
			if ("id".equals(spiderDataElement.getDataId())) {
				id = ((SpiderDataAtomic) spiderDataElement).getValue();
			}
		}
		if ("".equals(id)) {
			throw new ConverterException("No id was found in recordInfo convertion not possible");
		}
		return id;
	}

	private String findType(SpiderDataGroup recordInfo) {
		String type = "";
		for (SpiderDataElement spiderDataElement : recordInfo.getChildren()) {
			if ("type".equals(spiderDataElement.getDataId())) {
				type = ((SpiderDataAtomic) spiderDataElement).getValue();
			}
		}
		if ("".equals(type)) {
			throw new ConverterException("No type was found in recordInfo convertion not possible");
		}
		return type;
	}

	private void createRestLinks(String id, String type) {
		String url = type + "/" + id;

		for (Action action : spiderDataGroup.getActions()) {
			ActionLink actionLink = ActionLink.withAction(action);

			actionLink.setURL(baseURL + url);
			// TODO: add path etc.
			if (Action.READ.equals(action)) {
				actionLink.setRequestMethod("GET");
			} else if (Action.UPDATE.equals(action)) {
				actionLink.setRequestMethod("POST");
			} else {
				actionLink.setRequestMethod("DELETE");
			}

			actionLink.setAccept("application/uub+record+json");
			actionLink.setContentType("application/uub+record+json");
			restDataGroup.addActionLink(actionLink);
		}
	}

}
