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
import se.uu.ub.cora.spider.record.IncomingLinksReader;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordDeleter;
import se.uu.ub.cora.spider.record.RecordListIndexer;
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
	public IndexBatchJobCreatorSpy indexBatchJobCreator;
	public String recordType;
	public boolean throwRecordNotFoundException = false;
	public boolean throwDataException = false;

	@Override
	public RecordReader factorRecordReader() {
		spiderRecordReaderSpy = new SpiderRecordReaderSpy();
		return spiderRecordReaderSpy;
	}

	@Override
	public RecordListReader factorRecordListReader() {
		spiderRecordListReaderSpy = new SpiderRecordListReaderSpy();
		return spiderRecordListReaderSpy;
	}

	@Override
	public RecordCreator factorRecordCreator(String recordType) {
		this.recordType = recordType;
		spiderCreatorSpy = new SpiderCreatorSpy();
		return spiderCreatorSpy;
	}

	@Override
	public RecordUpdater factorRecordUpdater(String recordType) {
		this.recordType = recordType;
		spiderRecordUpdaterSpy = new SpiderRecordUpdaterSpy();
		spiderRecordUpdaterSpy.throwDataException = throwDataException;
		return spiderRecordUpdaterSpy;
	}

	@Override
	public RecordDeleter factorRecordDeleter() {
		spiderRecordDeleterSpy = new SpiderRecordDeleterSpy();
		return spiderRecordDeleterSpy;
	}

	@Override
	public Uploader factorUploader() {
		spiderUploaderSpy = new SpiderUploaderSpy();
		return spiderUploaderSpy;
	}

	@Override
	public Downloader factorDownloader() {
		spiderDownloaderSpy = new SpiderDownloaderSpy();
		return spiderDownloaderSpy;
	}

	@Override
	public RecordSearcher factorRecordSearcher() {
		spiderRecordSearcherSpy = new SpiderRecordSearcherSpy();
		return spiderRecordSearcherSpy;
	}

	@Override
	public IncomingLinksReader factorIncomingLinksReader() {
		spiderRecordIncomingLinksReaderSpy = new SpiderRecordIncomingLinksReaderSpy();
		return spiderRecordIncomingLinksReaderSpy;
	}

	@Override
	public RecordValidator factorRecordValidator() {
		spiderRecordValidatorSpy = new SpiderRecordValidatorSpy();
		spiderRecordValidatorSpy.throwRecordNotFoundException = throwRecordNotFoundException;
		return spiderRecordValidatorSpy;
	}

	@Override
	public String getDependencyProviderClassName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordListIndexer factorRecordListIndexer() {
		indexBatchJobCreator = new IndexBatchJobCreatorSpy();
		return indexBatchJobCreator;
	}

}
