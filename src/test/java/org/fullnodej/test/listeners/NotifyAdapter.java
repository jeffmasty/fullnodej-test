package org.fullnodej.test.listeners;

import static org.junit.Assert.fail;

import org.fullnodej.Notify;
import org.fullnodej.data.Block;
import org.fullnodej.data.WalletTx;
import org.fullnodej.test.FullnodeTest;

public class NotifyAdapter extends FullnodeTest implements Notify {

	@Override
	public void blockNotify(Block block) {
		log.info("blockNotify: " + block.getHeight());
	}

	@Override
	public void walletNotify(WalletTx tx) {
		log.info("walletNotify: " + tx.getTxid());
	}

	@Override
	public void alertNotify(String alert) {
		fail("Unexpected alert: " + alert);
	}

	@Override
	public void turnedOff() {
		if (!shutdown) fail("Notifications turned off.");
	}

}
