package org.fullnodej.test.misc;

import static org.fullnodej.Util.Constants.VERBOSE;
import static org.junit.Assert.*;

import org.fullnodej.data.Tx;
import org.fullnodej.data.VerboseTx;
import org.fullnodej.test.FullnodeTest;
import org.fullnodej.test.TESTDATA;
import org.junit.Test;

import lombok.Getter;


/**Lookup/Test a random confirmed transaction that is not a part of the wallet ledger, requires -txindex running on the rpc and
 * a LOOKUP_TX defined in {@link TESTDATA#PROPERTIES_FILE} */
public class TxIndex extends FullnodeTest {

	public static final String testnetTx="cc3a07613fd66694a2d9812b51cbb11e337f5411eaa0898e9791acc42ddc8d38";
	public static final String mainnetTx="9cb22794c6ae2e1242793c2638ae51e353d405d99bbc8402624bd6b60a6c4bac";
	public static final int blockheight = 406004; // roughly a day previous on mainnet to when this interface was written (re: mempool)
	@Getter private static boolean passed;

	@Test public void test() {

		if (!data.txindex) {
			skipped("no LOOKUP_TX given, assume rpc is NOT running -txindex");
			return;
		}
		final String id = node.getUtil().isTestnet() ? testnetTx : mainnetTx;
		Tx tx = null;
		VerboseTx verbose = null;
		try {
			tx = rpc.decoderawtransaction(rpc.getrawtransaction(id));
			verbose = rpc.getrawtransaction(id, VERBOSE);
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		assertEquals(id, tx.getTxid());
		assertEquals(id, verbose.getTxid());
		assertTrue(verbose.getBlocktime() < System.currentTimeMillis()); // before now
		assertEquals(rpc.getblockcount() + 1 - blockheight, verbose.getConfirmations()); // included in specific block
		try {
			String raw = rpc.getrawtransaction("noSoup4u");
			fail(raw);
		} catch (Throwable t) { /*expected*/}

		passed = true;
		passed();

	}

}
