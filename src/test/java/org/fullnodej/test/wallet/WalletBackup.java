package org.fullnodej.test.wallet;

import static org.junit.Assert.*;

import java.io.File;

import org.fullnodej.test.FullnodeTest;
import org.junit.Test;

public class WalletBackup extends FullnodeTest {

	public static final File BACKUP = new File(System.getProperty("user.home"), System.currentTimeMillis() + ".wallet.dat");

	@Test public void backupWallet() {

		assertFalse(BACKUP.exists());
		rpc.backupwallet(BACKUP.getAbsolutePath());
		assertTrue(BACKUP.exists());
		BACKUP.delete();
		passed();

	}

}
