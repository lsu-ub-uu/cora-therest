package se.uu.ub.cora.therest.iiif;

import se.uu.ub.cora.spider.binary.iiif.IiifImageReader;
import se.uu.ub.cora.spider.data.ResourceInputStream;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class IiifImageReaderSpy implements IiifImageReader {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public IiifImageReaderSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("readyBinary",
				() -> ResourceInputStream.withNameSizeInputStream(null, 0, null, null));
	}

	@Override
	public ResourceInputStream readImage(String identifier, String region, String size,
			String rotation, String quality, String format) {
		return (ResourceInputStream) MCR.addCallAndReturnFromMRV("identifier", identifier, "region",
				region, "size", size, "rotation", rotation, "quality", quality, "format", format);
	}

}
