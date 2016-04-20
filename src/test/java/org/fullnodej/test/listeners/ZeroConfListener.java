package org.fullnodej.test.listeners;

import static org.fullnodej.Util.Constants.DEFAULT_ACCOUNT;
import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.fullnodej.Notify;
import org.fullnodej.Util.Constants;
import org.fullnodej.data.Vout;
import org.fullnodej.data.WalletTx;
import org.fullnodej.data.WalletTxDetails;
import org.fullnodej.exception.ConfigurationException;
import org.junit.Test;

public class ZeroConfListener extends NotifyAdapter implements Notify {

	private String address;
	private CountDownLatch countDown = new CountDownLatch(1);
	private WalletTx zeroConf;

	@Test
	public void test() {


		try {
			address = rpc.getnewaddress(DEFAULT_ACCOUNT);
		} catch (Throwable e) {
			fail("looks like keypool depleted, refill or unlock wallet");
		}
		// address never seen before (except in keypool)
		assertNotEquals(address, rpc.getaccountaddress(DEFAULT_ACCOUNT));

		log.warn("Send bitcoins to " + address + " within the next " + data.notificationTimeoutMinutes + " minutes or the puppy gets it.");
		boolean success = false;
		try {
			node.getNotifications().attach(this);
			success = countDown.await(data.notificationTimeoutMinutes, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			fail("inturrupt: " + e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (ConfigurationException e) {
			fail("Fullnode Configuration: " + e.getMessage());
		}

		assertTrue("timed out", success);
		assertTrue("debit", zeroConf.getAmount().compareTo(BigDecimal.ZERO) > 0);

		for (Vout vout : rpc.getrawtransaction(zeroConf.getTxid(), Constants.VERBOSE).getVout())
			assertNotNull(rpc.gettxout(zeroConf.getTxid(), vout.getN(), true)); // guaranteed avail for zeroconfs

		passed("Cha-Ching: " + zeroConf.getAmount() + " BTC");
	}

	@Override
	public void walletNotify(WalletTx tx) {
		log.info("investigating: " + tx.getTxid());
		for (WalletTxDetails details : tx.getDetails())
			if (details.getAddress().equals(address)) {
				zeroConf = tx;
				countDown.countDown();
				return;
			}
	}

}
