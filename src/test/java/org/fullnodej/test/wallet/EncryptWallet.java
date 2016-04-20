package org.fullnodej.test.wallet;

import static org.junit.Assert.fail;

import org.fullnodej.test.FullnodeTest;
import org.fullnodej.test.TESTDATA;
import org.junit.Test;

/**<ul><li>WARNING: On success a previously unencrypted wallet becomes encrypted.
 * 	This test can only be run once against any given unencrypted wallet (encrypting it in the process)</li>
 *     <li>If wallet is already encrypted, this test is skipped (passes)</li>
 *     <li>If gets run while on unencrypted wallet and data.walletPassphrase is set to the empty string, this test is skipped (passes)</li></ul>*/
public class EncryptWallet extends FullnodeTest {

	public static final String ALERT_MESSAGE = "wallet encrypted; Bitcoin server stopping, restart to run with encrypted wallet. "
			+ "The keypool has been flushed, you need to make a new backup.";

	@Test public void test() {

		if (node.getWallet().isEncrypted()) {
			skipped("Already Encrypted");
			return;
		}
		final String passphrase = data.walletPassphrase;
		if (passphrase == null || passphrase.isEmpty()) {
			skipped("Unencrypted and walletPassphrase not set in " + TESTDATA.class.getSimpleName());
			return;
		}

		String shutdown = "Message NOT Received";
		try {
			shutdown = rpc.encryptwallet(passphrase);
		} catch (Throwable t) {
			log.error(t.getMessage());
		}

		log.error("*****************************************************");
		log.error("** FULLNODE: " + shutdown );
		log.error("*****************************************************");

		if (shutdown == null || shutdown.isEmpty()) {
			fail("After encrypting wallet, Rpc shutdown message NOT received in response.");
		}
		if (shutdown.equals(ALERT_MESSAGE)) passed("ENCRYPTED and SHUTDOWN");
	}

}
