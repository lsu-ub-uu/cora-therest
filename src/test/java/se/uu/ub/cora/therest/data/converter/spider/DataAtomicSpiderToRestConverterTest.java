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

public class DataAtomicSpiderToRestConverterTest {
	private SpiderDataAtomic spiderDataAtomic;
	private DataAtomicSpiderToRestConverter atomicSpiderToRestConverter;

	@BeforeMethod
	public void setUp() {
		spiderDataAtomic = SpiderDataAtomic.withNameInDataAndValue("nameInData", "value");
		atomicSpiderToRestConverter = DataAtomicSpiderToRestConverter
				.fromSpiderDataAtomic(spiderDataAtomic);

	}

	@Test
	public void testToRest() {
		RestDataAtomic restDataAtomic = atomicSpiderToRestConverter.toRest();
		assertEquals(restDataAtomic.getNameInData(), "nameInData");
		assertEquals(restDataAtomic.getValue(), "value");
	}

	@Test
	public void testToRestWithRepeatId() {
		spiderDataAtomic.setRepeatId("e4");
		RestDataAtomic restDataAtomic = atomicSpiderToRestConverter.toRest();
		assertEquals(restDataAtomic.getNameInData(), "nameInData");
		assertEquals(restDataAtomic.getValue(), "value");
		assertEquals(restDataAtomic.getRepeatId(), "e4");
	}
}
