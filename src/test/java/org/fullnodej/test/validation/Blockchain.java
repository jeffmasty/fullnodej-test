package org.fullnodej.test.validation;

import static org.junit.Assert.assertEquals;

import org.fullnodej.test.FullnodeTest;
import org.junit.Test;

public class Blockchain extends FullnodeTest {

	final String genesisTestnet = "000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943";
	final String genesisMain = "000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f";

	@Test public void test() {

		// Newest block
		assertEquals(rpc.getblockhash(rpc.getblockcount()), rpc.getbestblockhash());

		// Genesis block
		if (node.getUtil().isMainnet()) {
			assertEquals(0, rpc.getblock(genesisMain).getHeight());
			assertEquals(genesisMain, rpc.getblockhash(0));
		}
		else if (node.getUtil().isTestnet()) {
			assertEquals(0, rpc.getblock(genesisTestnet).getHeight());
			assertEquals(genesisTestnet, rpc.getblockhash(0));
		}
		else {
			log.warn("Missing test data to test genesis block of: " + rpc.getblockchaininfo().getChain());
			return;
		}

		passed();
	}

}
