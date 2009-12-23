/* 
 * Copyright 2009 Graham Allan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mutabilitydetector.benchmarks;

import static org.junit.Assert.assertEquals;
import static org.mutabilitydetector.ImmutableAssert.assertDefinitelyNotImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.CheckerRunner;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.MutableByAssigningAbstractTypeToField;
import org.mutabilitydetector.benchmarks.MutableByAssigningInterfaceToField;
import org.mutabilitydetector.checkers.AbstractTypeToFieldChecker;
import org.mutabilitydetector.checkers.IMutabilityChecker;



public class AbstractTypeToFieldCheckerTest {

	IMutabilityChecker checker;

	@Before
	public void setUp() {
		checker = new AbstractTypeToFieldChecker();
	}

	@Test
	public void testImmutableExamplePassesCheck() throws Exception {
		new CheckerRunner(null).run(checker, ImmutableExample.class);

		assertImmutable(checker.result());		
		assertEquals(checker.reasons().size(), 0);
	}
	
	@Test
	public void testMutableByAssigningInterfaceTypeToFieldFailsCheck() throws Exception {
		new CheckerRunner(null).run(checker, MutableByAssigningInterfaceToField.class);
		
		assertDefinitelyNotImmutable(checker.result());
	}
	
	@Test
	public void testMutableByAssigningAbstractClassToFieldFailsCheck() throws Exception {
		new CheckerRunner(null).run(checker, MutableByAssigningAbstractTypeToField.class);
		assertDefinitelyNotImmutable(checker.result());
	}

}
