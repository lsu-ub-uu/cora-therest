module se.uu.ub.cora.therest {
	requires transitive se.uu.ub.cora.logger;
	requires transitive se.uu.ub.cora.storage;
	requires transitive se.uu.ub.cora.spider;
	requires transitive jakarta.ws.rs;
	requires transitive jakarta.servlet;
	requires transitive jersey.media.multipart;
	requires transitive se.uu.ub.cora.httphandler;
	requires transitive se.uu.ub.cora.gatekeeperclient;
	requires transitive se.uu.ub.cora.solrsearch;
	requires transitive se.uu.ub.cora.searchstorage;
	requires se.uu.ub.cora.data;
	requires se.uu.ub.cora.converter;
	requires se.uu.ub.cora.initialize;
	requires se.uu.ub.cora.binary;
	requires se.uu.ub.cora.messaging;

	uses se.uu.ub.cora.storage.RecordStorageProvider;
	uses se.uu.ub.cora.storage.StreamStorageProvider;
	uses se.uu.ub.cora.storage.archive.RecordArchiveProvider;

	exports se.uu.ub.cora.therest.initialize;
}