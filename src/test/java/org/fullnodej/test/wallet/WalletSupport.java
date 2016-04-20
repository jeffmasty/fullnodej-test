package org.fullnodej.test.wallet;

import static org.junit.Assert.*;

import org.fullnodej.test.FullnodeTest;
import org.fullnodej.test.TESTDATA;
import org.junit.Test;

import lombok.Getter;

public class WalletSupport extends FullnodeTest {

	@Getter private static boolean passed;

	@Test public void test() {

		String pwd = data.walletPassphrase;
		if (pwd == null || pwd.isEmpty()) {
			if (node.getWallet().isEncrypted()) fail("Was not expecting an Encrypted Wallet. Set WALLET_PASSCODE in " + TESTDATA.PROPERTIES_FILE);
			assertTrue(sigcheck());
			passed("UNENCRYPTED");
			return;
		}

		if (node.getWallet().isLocked()) rpc.walletlock();
		assertFalse(sigcheck());
		try {
			sigcheck();
			fail("sigcheck() should have failed on a locked wallet");
		} catch (Throwable t) { /* expected */}

		rpc.walletpassphrase(data.walletPassphrase, 60);

		assertTrue(sigcheck());
		rpc.walletlock();
		assertFalse(sigcheck());

		// Does WalletSupport mean there are funds to play with?
		// assertTrue(rpc.getwalletinfo().getBalance().compareTo(BigDecimal.ZERO) > 0);

		passed = true;
		passed("Encrypted Wallet Locking/Unlocking");

	}
}
