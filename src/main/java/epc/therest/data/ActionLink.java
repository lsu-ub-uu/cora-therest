package epc.therest.data;

import epc.spider.data.Action;

public final class ActionLink {

	public static ActionLink withAction(Action read) {
		return new ActionLink(read);
	}

	private Action action;
	private String url;
	private String requestMethod;
	private String accept;
	private String contentType;

	private ActionLink(Action read) {
		this.action = read;
	}

	public Action getAction() {
		return action;
	}

	public void setURL(String url) {
		this.url = url;
	}

	public String getURL() {
		return url;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setAccept(String accept) {
		this.accept = accept;
	}

	public String getAccept() {
		return accept;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}

}
