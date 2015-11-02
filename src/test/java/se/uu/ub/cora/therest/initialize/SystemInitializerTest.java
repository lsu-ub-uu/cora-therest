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

package se.uu.ub.cora.therest.initialize;

import org.testng.annotations.Test;

public class SystemInitializerTest {
	@Test
	public void testInitializeSystem() {
		SystemInitializer systemInitializer = new SystemInitializer();
		systemInitializer.contextInitialized(null);
//		Assert.assertNotNull(object);
		//TODO: figure out what to test
	}
	@Test
	public void testDestroySystem(){
		SystemInitializer systemInitializer = new SystemInitializer();
		systemInitializer.contextDestroyed(null);
		//TODO: should we do something on destroy?
	}
}
