module se.uu.ub.cora.therest {
	requires se.uu.ub.cora.logger;
	requires se.uu.ub.cora.storage;
	requires se.uu.ub.cora.spider;
	requires java.activation;
	requires java.ws.rs;
	requires javax.servlet.api;
	requires jersey.media.multipart;

	uses se.uu.ub.cora.storage.RecordStorageProvider;
}