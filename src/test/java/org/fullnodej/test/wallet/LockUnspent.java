package org.fullnodej.test.wallet;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.fullnodej.data.Outpoint;
import org.fullnodej.data.Utxo;
import org.fullnodej.test.FullnodeTest;
import org.fullnodej.test.SendTransaction;
import org.junit.Test;

/**Locks the wallet and tries to run SendTransaction
 * Requires funds so that spending them in locked state fails*/
public class LockUnspent extends FullnodeTest {

	@Test public void test() {

		List<Outpoint> locked = rpc.listlockunspent();
		if (locked.size() != 0) {
			log.info("unlocking " + locked.size() + " locked utxo so I can lock them again");
		}

		rpc.lockunspent(true, locked);
		assertTrue(rpc.listlockunspent().isEmpty());

		assertTrue(rpc.getbalance().compareTo(data.sendToAmount) > 0);
		assertTrue(rpc.listunspent().size() > 0);

		List<Outpoint> outs = new ArrayList<>();
		for (Utxo utxo : rpc.listunspent()) {
			outs.add(new Outpoint(utxo.getTxid(), utxo.getVout()));
		}

		log.info("locked unspent: " + rpc.listlockunspent().size());

		boolean result = rpc.lockunspent(false, outs);
		log.info("locked: " + result);
		log.info("locked unspent: " + rpc.listlockunspent().size());

		if (rpc.listlockunspent().isEmpty()) fail("should have been locked");

		try {
			new SendTransaction().setSilent(true).test();
			fail("Should have been locked");
		} catch (Throwable t) {
			// expected
		} finally {
			rpc.lockunspent(true, rpc.listlockunspent());
		}
		assertTrue(rpc.listlockunspent().isEmpty());
		passed();
	}

}
