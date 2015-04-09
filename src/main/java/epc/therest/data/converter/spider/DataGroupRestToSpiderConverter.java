package epc.therest.data.converter.spider;

import java.util.Map;
import java.util.Map.Entry;

import epc.spider.data.SpiderDataAtomic;
import epc.spider.data.SpiderDataGroup;
import epc.therest.data.ActionLink;
import epc.therest.data.RestDataAtomic;
import epc.therest.data.RestDataElement;
import epc.therest.data.RestDataGroup;

public final class DataGroupRestToSpiderConverter {
	private RestDataGroup restDataGroup;
	private SpiderDataGroup spiderDataGroup;

	public static DataGroupRestToSpiderConverter fromRestDataGroup(RestDataGroup restDataGroup) {
		return new DataGroupRestToSpiderConverter(restDataGroup);
	}

	private DataGroupRestToSpiderConverter(RestDataGroup restDataGroup) {
		this.restDataGroup = restDataGroup;
	}

	public SpiderDataGroup toSpider() {
		spiderDataGroup = SpiderDataGroup.withDataId(restDataGroup.getDataId());
		addAttributesToSpiderGroup();
		addActionsToSpiderGroup();
		addChildrenToSpiderGroup();
		return spiderDataGroup;
	}

	private void addAttributesToSpiderGroup() {
		Map<String, String> attributes = restDataGroup.getAttributes();
		for (Entry<String, String> entry : attributes.entrySet()) {
			spiderDataGroup.addAttributeByIdWithValue(entry.getKey(), entry.getValue());
		}
	}

	private void addActionsToSpiderGroup() {
		for (ActionLink actionLink : restDataGroup.getActionLinks()) {
			spiderDataGroup.addAction(actionLink.getAction());
		}
	}

	private void addChildrenToSpiderGroup() {
		for (RestDataElement restDataElement : restDataGroup.getChildren()) {
			addChildToSpiderGroup(restDataElement);
		}
	}

	private void addChildToSpiderGroup(RestDataElement restDataElement) {
		if (restDataElement instanceof RestDataGroup) {
			addGroupChild(restDataElement);
		} else {
			addAtomicChild(restDataElement);
		}
	}

	private void addGroupChild(RestDataElement restDataElement) {
		SpiderDataGroup spiderDataGroupChild = DataGroupRestToSpiderConverter.fromRestDataGroup(
				(RestDataGroup) restDataElement).toSpider();
		spiderDataGroup.addChild(spiderDataGroupChild);
	}

	private void addAtomicChild(RestDataElement restDataElement) {
		SpiderDataAtomic spiderDataAtomic = DataAtomicRestToSpiderConverter.fromRestDataAtomic(
				(RestDataAtomic) restDataElement).toSpider();
		spiderDataGroup.addChild(spiderDataAtomic);
	}

}
