package epc.therest.data;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class RestDataRecord {
	private RestDataGroup restDataGroup;
	private Set<String> keys = new LinkedHashSet<>();
	private Map<String, ActionLink> actionLinks = new LinkedHashMap<>();

	public static RestDataRecord withRestDataGroup(RestDataGroup restDataGroup) {
		return new RestDataRecord(restDataGroup);
	}

	private RestDataRecord(RestDataGroup restDataGroup) {
		this.restDataGroup = restDataGroup;
	}

	public RestDataGroup getRestDataGroup() {
		return restDataGroup;
	}

	public void addKey(String key) {
		keys.add(key);
	}

	public Set<String> getKeys() {
		return keys;
	}

	public void addActionLink(String key, ActionLink actionLink) {
		actionLinks.put(key, actionLink);
	}

	public Map<String, ActionLink> getActionLinks() {
		return actionLinks;
	}

	public ActionLink getActionLink(String key) {
		return actionLinks.get(key);
	}
}
