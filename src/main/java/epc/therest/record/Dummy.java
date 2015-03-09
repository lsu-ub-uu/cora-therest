package epc.therest.record;

import java.util.HashMap;
import java.util.Map;

public class Dummy {
	public final String dataId;
	private Map<String, String> out = new HashMap<>();

	public void setOut(Map<String, String> out) {
//		this.out = out;
	}
	public Dummy() {
		dataId = "dataIdSetInPublicConstructor";
	}
	public Dummy(String dataId) {
		this.dataId = dataId;
		out.put("myKey", "myValue");
	}

	public Map<String, String> getOut() {
		return out;
	}
}
