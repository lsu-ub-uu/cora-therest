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
import se.uu.ub.cora.spider.record.Downloader;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordDeleter;
import se.uu.ub.cora.spider.record.IncomingLinksReader;
import se.uu.ub.cora.spider.record.RecordListReader;
import se.uu.ub.cora.spider.record.RecordReader;
import se.uu.ub.cora.spider.record.RecordSearcher;
import se.uu.ub.cora.spider.record.RecordUpdater;
import se.uu.ub.cora.spider.record.RecordValidator;
import se.uu.ub.cora.spider.record.Uploader;

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
	public String recordType;

	@Override
	public RecordReader factorSpiderRecordReader() {
		spiderRecordReaderSpy = new SpiderRecordReaderSpy();
		return spiderRecordReaderSpy;
	}

	@Override
	public RecordListReader factorSpiderRecordListReader() {
		spiderRecordListReaderSpy = new SpiderRecordListReaderSpy();
		return spiderRecordListReaderSpy;
	}

	@Override
	public RecordCreator factorSpiderRecordCreator(String recordType) {
		this.recordType = recordType;
		spiderCreatorSpy = new SpiderCreatorSpy();
		return spiderCreatorSpy;
	}

	@Override
	public RecordUpdater factorSpiderRecordUpdater(String recordType) {
		this.recordType = recordType;
		spiderRecordUpdaterSpy = new SpiderRecordUpdaterSpy();
		return spiderRecordUpdaterSpy;
	}

	@Override
	public RecordDeleter factorSpiderRecordDeleter() {
		spiderRecordDeleterSpy = new SpiderRecordDeleterSpy();
		return spiderRecordDeleterSpy;
	}

	@Override
	public Uploader factorSpiderUploader() {
		spiderUploaderSpy = new SpiderUploaderSpy();
		return spiderUploaderSpy;
	}

	@Override
	public Downloader factorSpiderDownloader() {
		spiderDownloaderSpy = new SpiderDownloaderSpy();
		return spiderDownloaderSpy;
	}

	@Override
	public RecordSearcher factorSpiderRecordSearcher() {
		spiderRecordSearcherSpy = new SpiderRecordSearcherSpy();
		return spiderRecordSearcherSpy;
	}

	@Override
	public IncomingLinksReader factorSpiderRecordIncomingLinksReader() {
		spiderRecordIncomingLinksReaderSpy = new SpiderRecordIncomingLinksReaderSpy();
		return spiderRecordIncomingLinksReaderSpy;
	}

	@Override
	public RecordValidator factorSpiderRecordValidator() {
		spiderRecordValidatorSpy = new SpiderRecordValidatorSpy();
		return spiderRecordValidatorSpy;
	}

	@Override
	public String getDependencyProviderClassName() {
		// TODO Auto-generated method stub
		return null;
	}

}
