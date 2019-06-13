module se.uu.ub.cora.therest {
	requires se.uu.ub.cora.logger;
	requires se.uu.ub.cora.storage;
	requires se.uu.ub.cora.spider;
	requires java.activation;
	requires java.ws.rs;
	requires javax.servlet.api;
	requires jersey.media.multipart;
	requires se.uu.ub.cora.httphandler;
	requires se.uu.ub.cora.metacreator;
	requires se.uu.ub.cora.gatekeeperclient;
	requires se.uu.ub.cora.solrsearch;

	uses se.uu.ub.cora.storage.RecordStorageProvider;
	uses se.uu.ub.cora.storage.StreamStorageProvider;
	uses se.uu.ub.cora.storage.RecordIdGeneratorProvider;
	uses se.uu.ub.cora.storage.MetadataStorageProvider;
}