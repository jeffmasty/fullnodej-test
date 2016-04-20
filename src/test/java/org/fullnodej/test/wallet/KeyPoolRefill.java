package org.fullnodej.test.wallet;

import org.fullnodej.test.FullnodeTest;
import org.fullnodej.test.FullnodeTest.OpenWalletRequired;
import org.junit.Test;

public class KeyPoolRefill extends FullnodeTest implements OpenWalletRequired {

	@Test
	public void test() {

		final int size = rpc.getwalletinfo().getKeypoolsize();
		rpc.keypoolrefill(size + 10);
		final int newSize = rpc.getwalletinfo().getKeypoolsize();
		assert(newSize > size);
		passed("keypool old: " + size + " new: " + newSize);

	}

}
