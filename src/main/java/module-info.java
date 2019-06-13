module se.uu.ub.cora.therest {
	requires transitive se.uu.ub.cora.logger;
	requires transitive se.uu.ub.cora.storage;
	requires transitive se.uu.ub.cora.spider;
	requires transitive java.activation;
	requires transitive java.ws.rs;
	requires transitive javax.servlet.api;
	requires transitive jersey.media.multipart;
	requires transitive se.uu.ub.cora.httphandler;
	requires transitive se.uu.ub.cora.metacreator;
	requires transitive se.uu.ub.cora.gatekeeperclient;
	requires transitive se.uu.ub.cora.solrsearch;

	uses se.uu.ub.cora.storage.RecordStorageProvider;
	uses se.uu.ub.cora.storage.StreamStorageProvider;
	uses se.uu.ub.cora.storage.RecordIdGeneratorProvider;
	uses se.uu.ub.cora.storage.MetadataStorageProvider;

	exports se.uu.ub.cora.therest.initialize;
}