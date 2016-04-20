package org.fullnodej.test.wallet;

import org.fullnodej.test.FullnodeTest;
import org.fullnodej.test.FullnodeTest.OpenWalletRequired;
import org.junit.Assert;
import org.junit.Test;

public class WalletPassphraseChange extends FullnodeTest implements OpenWalletRequired {

	private static final String NEW_PWD = WalletPassphraseChange.class.getCanonicalName();

	@Test public void test() {

		if (notSet(data.walletPassphrase)) {
			skipped("unencrypted wallet, data.walletPassphrase not set");
			return;
		}

		Assert.assertTrue(sigcheck());
		log.warn("Setting new wallet passphrase to: " + NEW_PWD); // org.fullnodej.test.wallet.WalletPassphraseChange
		rpc.walletpassphrasechange(data.walletPassphrase, NEW_PWD);

		try {
			rpc.walletlock();

			try {
				rpc.walletpassphrase(data.walletPassphrase, 1);
				Assert.assertTrue("Should not be here", sigcheck());
				Assert.fail();
			} catch (Throwable e) {
				log.info("Changed (original passphrase currently fails)");
			}

		} finally {
			// set back to original
			try {
				rpc.walletpassphrase(NEW_PWD, 5);
				rpc.walletpassphrasechange(NEW_PWD, data.walletPassphrase);
				Assert.assertTrue(sigcheck());
				log.warn("Wallet passphrase back to original.");
			} catch (Throwable e) {
				log.error("SETTING WALLET PASSPHRASE BACK TO ORIGINAL FAILED: " + e.getMessage());
				Assert.fail(e.getMessage());
			} finally {
				rpc.walletlock();
			}
		}
		Assert.assertFalse(sigcheck());
		passed();

	}

}
