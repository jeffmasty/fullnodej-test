package org.fullnodej.test.listeners;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.fullnodej.Notify;
import org.fullnodej.data.Block;
import org.fullnodej.data.Catchup;
import org.fullnodej.data.WalletTx;
import org.fullnodej.exception.ConfigurationException;
import org.junit.Test;

public class ConfirmationListener extends NotifyAdapter implements Notify {

	String txid;
	final CountDownLatch countDown = new CountDownLatch(1);

	@Test public void test() {

		// check if there are any unconfirmed txs in the wallet
		for (WalletTx recent : rpc.listtransactions())
			if (recent.getConfirmations() == 0) {
				txid = recent.getTxid();
				break;
			}

		if (txid == null) {
			skipped("No unconfirmed transactions");
			return;
		}
		log.info("Found unconfirmed tx: " + txid);
		int startHeight = rpc.getblockcount();

		try {
			node.getNotifications().attach(this);
			log.warn("Wallet Listener on port:" + data.listenerPort + " appears OK");
		} catch (IOException | ConfigurationException e) {
			fail(e.getMessage());
			return;
		}

		try {
			log.warn("The current height is " + startHeight + ". I await() the miners... ");
			boolean groundZero = countDown.await(2, TimeUnit.HOURS);
			assertTrue(groundZero);
		} catch (InterruptedException e) {
			fail(e.getMessage());
			return;
		}
		int finishHeight = rpc.getblockcount();
		assertTrue(finishHeight > startHeight);

		WalletTx confirmed;
		try {
			confirmed = rpc.gettransaction(txid);
		} catch (Throwable e) {
			fail(e.getMessage());
			return;
		}
		assertTrue(confirmed.getConfirmations() >= data.confirmations);

		// we now have data to test against rpc.listsinceblock()...
		Catchup sinceStart = rpc.listsinceblock(rpc.getblockhash(startHeight), 1, false);
		Catchup sinceFinish = rpc.listsinceblock(rpc.getblockhash(finishHeight), 1, false);
		assertTrue(sinceStart.getTransactions().size() > sinceFinish.getTransactions().size());

		passed("confirmed at height " + confirmed.getBlockindex() + ": " + txid);
	}

	@Override
	public void blockNotify(Block block) {
		log.info("block " + block.getHeight() + " received");
		WalletTx tx = rpc.gettransaction(txid);
		if (isConfirmed(tx))
			countDown.countDown();
		else {
			log.info("still waiting for " + (data.confirmations - tx.getConfirmations()) + " confirmation(s)");
		}
	}

}
