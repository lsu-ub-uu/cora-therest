/*
 * Copyright 2016, 2019, 2022 Uppsala University Library
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

import se.uu.ub.cora.spider.binary.Downloader;
import se.uu.ub.cora.spider.binary.Uploader;
import se.uu.ub.cora.spider.binary.iiif.IiifReader;
import se.uu.ub.cora.spider.dependency.SpiderInstanceFactory;
import se.uu.ub.cora.spider.record.DecoratedRecordReader;
import se.uu.ub.cora.spider.record.IncomingLinksReader;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordDeleter;
import se.uu.ub.cora.spider.record.RecordListIndexer;
import se.uu.ub.cora.spider.record.RecordListReader;
import se.uu.ub.cora.spider.record.RecordReader;
import se.uu.ub.cora.spider.record.RecordSearcher;
import se.uu.ub.cora.spider.record.RecordUpdater;
import se.uu.ub.cora.spider.record.RecordValidator;

public class OldSpiderInstanceFactorySpy implements SpiderInstanceFactory {

	public SpiderDownloaderSpy spiderDownloaderSpy;
	public SpiderUploaderSpy spiderUploaderSpy;
	public SpiderCreatorOldSpy spiderCreatorSpy;
	public SpiderRecordUpdaterSpy spiderRecordUpdaterSpy;
	public SpiderRecordDeleterSpy spiderRecordDeleterSpy;
	public SpiderRecordReaderSpy spiderRecordReaderSpy;
	public SpiderRecordValidatorSpy spiderRecordValidatorSpy;
	public SpiderRecordIncomingLinksReaderSpy spiderRecordIncomingLinksReaderSpy;
	public SpiderRecordListReaderSpy spiderRecordListReaderSpy;
	public SpiderRecordSearcherSpy spiderRecordSearcherSpy;
	public IndexBatchJobCreatorSpy spiderRecordListIndexerSpy;
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
	public RecordCreator factorRecordCreator() {
		spiderCreatorSpy = new SpiderCreatorOldSpy();
		return spiderCreatorSpy;
	}

	@Override
	public RecordUpdater factorRecordUpdater() {
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
		return new SpiderUploaderSpy();
	}

	@Override
	public Downloader factorDownloader() {
		return new SpiderDownloaderSpy();
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
		spiderRecordListIndexerSpy = new IndexBatchJobCreatorSpy();
		return spiderRecordListIndexerSpy;
	}

	@Override
	public IiifReader factorIiifReader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DecoratedRecordReader factorDecoratedRecordReader() {
		// TODO Auto-generated method stub
		return null;
	}

}
