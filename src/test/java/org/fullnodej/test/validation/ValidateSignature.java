package org.fullnodej.test.validation;

import org.fullnodej.test.FullnodeTest;
import org.junit.Assert;
import org.junit.Test;

public class ValidateSignature extends FullnodeTest {

	@Test public void test() {

		if (notSet(data.sigaddress) || notSet(data.signature) || notSet(data.sigmessage)) {
			skipped("sig test data not found");
			return;
		}
		Assert.assertTrue(rpc.verifymessage(data.sigaddress, data.signature, data.sigmessage));
		passed();
	}

}
