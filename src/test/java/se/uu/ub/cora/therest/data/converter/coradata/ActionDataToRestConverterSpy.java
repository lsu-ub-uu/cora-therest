package se.uu.ub.cora.therest.data.converter.coradata;

import java.util.HashMap;
import java.util.Map;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.therest.data.ActionLink;

public class ActionDataToRestConverterSpy implements ActionDataToRestConverter {

	public boolean toRestWasCalled = false;
	public Map<String, ActionLink> actionLinks = new HashMap<>();

	@Override
	public Map<String, ActionLink> toRest() {
		ActionLink read = ActionLink.withAction(Action.READ);
		ActionLink create = ActionLink.withAction(Action.CREATE);
		actionLinks.put("read", read);
		actionLinks.put("create", create);
		toRestWasCalled = true;
		return actionLinks;
	}

}
