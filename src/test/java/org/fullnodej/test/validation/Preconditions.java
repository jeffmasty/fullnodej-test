package org.fullnodej.test.validation;

import static org.junit.Assert.*;

import org.fullnodej.test.FullnodeTest;
import org.junit.Test;

public class Preconditions extends FullnodeTest {

	@Test public void test() {

		assertTrue(rpc.getconnectioncount() > 0); // ensure rpc is p2p connected
		assertTrue(rpc.getblockcount() > 400000l); // t/f for regtest, but true as of this writing for test and main chains
		assertTrue(rpc.getdifficulty().floatValue() > 0f); // ensure some mining is happening somewhere on the chain
		assertTrue(rpc.verifychain(1, 10)); // assert that the rpc is working with verifiable data
		passed("balance: " + rpc.getbalance() + " unconfirmed: " + rpc.getunconfirmedbalance());
		assertEquals(rpc.getblockchaininfo().getChain(), data.chain);

		// default is not mining, possibly switch to mining for a test or regtest
		assertEquals(rpc.getgenerate(), false /*data.generate (if true, run mining test)*/);

	}

}
