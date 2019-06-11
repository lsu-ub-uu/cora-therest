/*
 * Copyright 2015 Uppsala University Library
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

package se.uu.ub.cora.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.therest.data.RestDataAtomic;

public class DataAtomicRestToSpiderConverterTest {
	private RestDataAtomic restDataAtomic;
	private DataAtomicRestToSpiderConverter converter;

	@BeforeMethod
	public void setUp() {
		restDataAtomic = RestDataAtomic.withNameInDataAndValue("nameInData", "value");
		converter = DataAtomicRestToSpiderConverter.fromRestDataAtomic(restDataAtomic);

	}

	@Test
	public void testToSpider() {
		SpiderDataAtomic spiderDataAtomic = converter.toSpider();
		assertEquals(spiderDataAtomic.getNameInData(), "nameInData");
		assertEquals(spiderDataAtomic.getValue(), "value");
	}

	@Test
	public void testToSpiderWithRepeatId() {
		restDataAtomic.setRepeatId("x3");
		SpiderDataAtomic spiderDataAtomic = converter.toSpider();
		assertEquals(spiderDataAtomic.getNameInData(), "nameInData");
		assertEquals(spiderDataAtomic.getValue(), "value");
		assertEquals(spiderDataAtomic.getRepeatId(), "x3");
	}
}
