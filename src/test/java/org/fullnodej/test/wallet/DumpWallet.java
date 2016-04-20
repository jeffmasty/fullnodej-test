package org.fullnodej.test.wallet;

import static org.junit.Assert.*;

import java.io.File;

import org.fullnodej.test.FullnodeTest;
import org.fullnodej.test.FullnodeTest.OpenWalletRequired;
import org.junit.Test;


public class DumpWallet extends FullnodeTest implements OpenWalletRequired {

	public static final File DUMP_FILE = new File(
			System.getProperty("user.home"), "fullnodej-dump.wallet.dat");

	@Test public void test() {
		assertFalse(DUMP_FILE.exists());
		try {
			rpc.dumpwallet(DUMP_FILE.getAbsolutePath());
		} catch (Throwable e) {
			fail(e.getMessage());
		} finally {
			if (DUMP_FILE.isFile())
				assertTrue(DUMP_FILE.delete());
		}
		passed();
	}

}
