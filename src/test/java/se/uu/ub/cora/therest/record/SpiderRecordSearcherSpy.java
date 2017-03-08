package se.uu.ub.cora.therest.record;

import se.uu.ub.cora.spider.authentication.AuthenticationException;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataList;
import se.uu.ub.cora.spider.record.DataException;
import se.uu.ub.cora.spider.record.SpiderRecordSearcher;
import se.uu.ub.cora.spider.record.storage.RecordNotFoundException;

public class SpiderRecordSearcherSpy implements SpiderRecordSearcher {

	public String authToken;
	public String searchId;
	public SpiderDataGroup searchData;

	@Override
	public SpiderDataList search(String authToken, String searchId, SpiderDataGroup searchData) {
		this.authToken = authToken;
		this.searchId = searchId;
		this.searchData = searchData;
		if ("nonExistingToken".equals(authToken)) {
			throw new AuthenticationException("User not authenticated");
		}
		if ("dummyNonAuthorizedToken".equals(authToken)) {
			throw new AuthorizationException("User not authorized");
		}
		if ("aSearchId_NOT_FOUND".equals(searchId)) {
			throw new RecordNotFoundException("Record does not exist");
		}
		if ("aSearchId_INVALID_DATA".equals(searchId)) {
			throw new DataException("SearchData is invalid");
		}
		SpiderDataList searchResult = SpiderDataList.withContainDataOfType("mix");
		searchResult.setFromNo("0");
		searchResult.setToNo("1");
		searchResult.setTotalNo("1");
		return searchResult;
	}

}
