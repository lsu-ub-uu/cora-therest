/*
 * Copyright 2016, 2019 Uppsala University Library
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

package se.uu.ub.cora.therest.record;

import se.uu.ub.cora.spider.dependency.SpiderInstanceFactory;
import se.uu.ub.cora.spider.record.SpiderDownloader;
import se.uu.ub.cora.spider.record.SpiderRecordCreator;
import se.uu.ub.cora.spider.record.SpiderRecordDeleter;
import se.uu.ub.cora.spider.record.SpiderRecordIncomingLinksReader;
import se.uu.ub.cora.spider.record.SpiderRecordListReader;
import se.uu.ub.cora.spider.record.SpiderRecordReader;
import se.uu.ub.cora.spider.record.SpiderRecordSearcher;
import se.uu.ub.cora.spider.record.SpiderRecordUpdater;
import se.uu.ub.cora.spider.record.SpiderRecordValidator;
import se.uu.ub.cora.spider.record.SpiderUploader;

public class SpiderInstanceFactorySpy implements SpiderInstanceFactory {

	public SpiderDownloaderSpy spiderDownloaderSpy;
	public SpiderUploaderSpy spiderUploaderSpy;
	public SpiderCreatorSpy spiderCreatorSpy;
	public SpiderRecordUpdaterSpy spiderRecordUpdaterSpy;
	public SpiderRecordDeleterSpy spiderRecordDeleterSpy;
	public SpiderRecordReaderSpy spiderRecordReaderSpy;
	public SpiderRecordValidatorSpy spiderRecordValidatorSpy;
	public SpiderRecordIncomingLinksReaderSpy spiderRecordIncomingLinksReaderSpy;
	public SpiderRecordListReaderSpy spiderRecordListReaderSpy;
	public SpiderRecordSearcherSpy spiderRecordSearcherSpy;

	@Override
	public SpiderRecordReader factorSpiderRecordReader() {
		spiderRecordReaderSpy = new SpiderRecordReaderSpy();
		return spiderRecordReaderSpy;
	}

	@Override
	public SpiderRecordListReader factorSpiderRecordListReader() {
		spiderRecordListReaderSpy = new SpiderRecordListReaderSpy();
		return spiderRecordListReaderSpy;
	}

	@Override
	public SpiderRecordCreator factorSpiderRecordCreator() {
		spiderCreatorSpy = new SpiderCreatorSpy();
		return spiderCreatorSpy;
	}

	@Override
	public SpiderRecordUpdater factorSpiderRecordUpdater() {
		spiderRecordUpdaterSpy = new SpiderRecordUpdaterSpy();
		return spiderRecordUpdaterSpy;
	}

	@Override
	public SpiderRecordDeleter factorSpiderRecordDeleter() {
		spiderRecordDeleterSpy = new SpiderRecordDeleterSpy();
		return spiderRecordDeleterSpy;
	}

	@Override
	public SpiderUploader factorSpiderUploader() {
		spiderUploaderSpy = new SpiderUploaderSpy();
		return spiderUploaderSpy;
	}

	@Override
	public SpiderDownloader factorSpiderDownloader() {
		spiderDownloaderSpy = new SpiderDownloaderSpy();
		return spiderDownloaderSpy;
	}

	@Override
	public SpiderRecordSearcher factorSpiderRecordSearcher() {
		spiderRecordSearcherSpy = new SpiderRecordSearcherSpy();
		return spiderRecordSearcherSpy;
	}

	@Override
	public SpiderRecordIncomingLinksReader factorSpiderRecordIncomingLinksReader() {
		spiderRecordIncomingLinksReaderSpy = new SpiderRecordIncomingLinksReaderSpy();
		return spiderRecordIncomingLinksReaderSpy;
	}

	@Override
	public SpiderRecordValidator factorSpiderRecordValidator() {
		spiderRecordValidatorSpy = new SpiderRecordValidatorSpy();
		return spiderRecordValidatorSpy;
	}

	@Override
	public String getDependencyProviderClassName() {
		// TODO Auto-generated method stub
		return null;
	}

}
