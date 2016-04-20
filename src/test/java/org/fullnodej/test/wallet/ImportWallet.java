package org.fullnodej.test.wallet;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.fullnodej.test.FullnodeTest;
import org.fullnodej.test.FullnodeTest.OpenWalletRequired;
import org.junit.Test;

/** Import a wallet with at least 1 new private key sitting on it */
public class ImportWallet extends FullnodeTest implements OpenWalletRequired {

	@Test public void test() {
		if (notSet(data.importWallet)) {
			skipped();
			return;
		}
		// File wallet = new File(data.importWallet);
		// if (!wallet.isFile()) fail("not a file (data.importWallet): " + wallet.getAbsolutePath());

		// how many private keys currently?
		final int keyCount = node.getUtil().allAddresses().size();
		// What's the current balance?
		final BigDecimal balance = rpc.getbalance();

		// Import Wallet
		try {
			rpc.importwallet(data.importWallet);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			fail(t.getMessage());
		}

		int newCount = node.getUtil().allAddresses().size();
		try { // wallet backup may take some time
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(newCount > keyCount);
		final BigDecimal newBalance = rpc.getbalance();
		if (newBalance.equals(balance))
			log.warn("Imported " + (newCount - keyCount) + " keys but balance did not change.");
		else passed();
	}

}
