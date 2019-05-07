package se.uu.ub.cora.therest.data.converter.spider;

import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.therest.data.converter.ConverterInfo;

public interface SpiderToRestConverterFactory {

	SpiderToRestConverter factorForSpiderDataGroupWithConverterInfo(SpiderDataGroup spiderDataGroup,
			ConverterInfo converterInfo);

}
