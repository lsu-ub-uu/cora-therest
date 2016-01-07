package se.uu.ub.cora.therest.data;

import java.util.LinkedHashMap;
import java.util.Map;

public class RestDataGroupRecordLink extends RestDataGroup{

    private Map<String, ActionLink> actionLinks = new LinkedHashMap<>();

    public static RestDataGroupRecordLink withNameInData(String nameInData){
        return new RestDataGroupRecordLink(nameInData);
    }

    private RestDataGroupRecordLink(String nameInData) {
        super(nameInData);
    }

    public void addActionLink(String key, ActionLink actionLink) {
        actionLinks.put(key, actionLink);
    }

    public ActionLink getActionLink(String key) {
        return actionLinks.get(key);
    }

    public Map<String, ActionLink> getActionLinks() {
        return actionLinks;
    }

    public void setActionLinks(Map<String, ActionLink> actionLinks) {
        this.actionLinks = actionLinks;
    }
}
