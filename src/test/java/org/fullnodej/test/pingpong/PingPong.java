package org.fullnodej.test.pingpong;

import static java.util.concurrent.TimeUnit.*;
import static org.fullnodej.Util.Constants.DEFAULT_ACCOUNT;
import static org.fullnodej.test.pingpong.PingPongBall.Type.*;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

import org.fullnodej.Fullnode;
import org.fullnodej.Rpc;
import org.fullnodej.RpcParams;
import org.fullnodej.test.FullnodeTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** <ol><li>node2 sends to node1</li>
		<li>both nodes ack and confirm</li>
		<li>node1 sends to node2</li>
		<li>both nodes ack</li></ol> */
public class PingPong extends FullnodeTest {

	final static CountDownLatch pingRecieved = new CountDownLatch(2);
	final static CountDownLatch pingConfirmed = new CountDownLatch(2);
	final static CountDownLatch pongReceived = new CountDownLatch(2);

	private static PingPongBall serve;
	static PingPongBall serve() { return serve; }

	private static Fullnode node2;
	private static Rpc rpc2;
	private PingPongBall ping, pong;
	private PingPongPlayer player1, player2;

	@Before
	public void connect() throws RuntimeException {
		try {
			node2 = new Fullnode(new RpcParams(data.rpc2password, data.rpc2port), data.listenerPort2);
			rpc2 = node2.rpc;
			log.info("Dually noded: " + (rpc.getinfo() != null && rpc2.getinfo() != null && !rpc.equals(rpc2))
					+ ", confirmations required: " + data.confirmations);

			ping = new PingPongBall(PING, node2.getUtil().useEstimatedFees(), rpc.getaccountaddress(DEFAULT_ACCOUNT));
			player1 = new PingPongPlayer(PING, data, rpc2);
			node2.getNotifications().attach(player1);
			log.info("Ping player on port :" + node2.getNotifications().getListenerPort());

			pong = new PingPongBall(PONG, node.getUtil().useEstimatedFees(), rpc2.getaccountaddress(DEFAULT_ACCOUNT));
			player2 = new PingPongPlayer(PONG, data, rpc);
			node.getNotifications().attach(player2);
			log.info("Pong player on port :" + node.getNotifications().getListenerPort() + ", Let's Play...");

			log.info(" ################################");
			log.info(" #  PING PONG !                 #");
			log.info(" #            ,;;;!!!!!;;.      #");
			log.info(" #          :!!!!!!!!!!!!!!;    #");
			log.info(" #        :!!!!!!!!!!!!!!!!!;   #");
			log.info(" #       ;!!!!!!!!!!!!!!!!!!!;  #");
			log.info(" #      ;!!!!!!!!!!!!!!!!!!!!!  #");
			log.info(" #      ;!!!!!!!!!!!!!!!!!!!!'  #");
			log.info(" #      ;!!!!!!!!!!!!!!!!!!!'   #");
			log.info(" #       :!!!!!!!!!!!!!!!!'     #");
			log.info(" #        ,!!!!!!!!!!!!!''      #");
			log.info(" #     ,;!!!''''''''''          #");
			log.info(" #   .!!!!'            ._       #");
			log.info(" #  !!!!`              (_)      #");
			log.info(" ################################");

		} catch (Throwable t) {
			if (t instanceof RuntimeException) { throw (RuntimeException)t; }
			throw new RuntimeException(t);
		}
		serve = ping;
	}

	@Test
	public void test() {
		assertNotNull(rpc.getinfo()); assertNotNull(rpc2.getinfo());// connected
		assertNotEquals(rpc, rpc2); // to different servers
		assertNotEquals(node.getNotifications().getListenerPort(), node2.getNotifications().getListenerPort());

		// Send Ping Tx
		final BigDecimal pingAmount = data.pingPongAmount.subtract(ping.fee);
		assertTrue(rpc2.getbalance().doubleValue() > pingAmount.doubleValue());
		log.warn("Pinging " + pingAmount + " to " + ping.toAddress);
		try {
			// open ping wallet
			assertTrue(node2.getWallet().unlock(data.wallet2Passphrase, 5));
			// send ping TX
			ping.txid = rpc2.sendtoaddress(ping.toAddress, pingAmount, "ping's serve", "in pong's court");
			log.info("Ping txid: " + ping.txid);
		} finally {
			node2.getWallet().lock();
		}

		try {
			assertTrue("timeout", pingRecieved.await(10, MINUTES));
			log.info("Ping received at height " + rpc.getblockcount() + ", I await() confirmation...");
			assertTrue("timeout", pingConfirmed.await(1, HOURS));
			log.info("Ping Confirmed. The serve passes to Pong.");
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}

		serve = pong;

		// Send Pong Tx
		final BigDecimal pongAmount = data.pingPongAmount.subtract(ping.fee).subtract(pong.fee);
		log.warn("Ponging " + pongAmount + " to " + pong.toAddress);
		try {
			// open pong wallet
			assertTrue(node.getWallet().unlock(data.walletPassphrase, 5));
			// send ping TX
			pong.txid = rpc.sendtoaddress(pong.toAddress, pongAmount, "pong's serve", "in ping's court");
			log.info("Pong txid: " + pong.txid);
		} finally {
			node.getWallet().lock();
		}

		try {
			assertTrue("timeout", pongReceived.await(1, HOURS));
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}

		passed("Pong Received. Thank you for playing, I hope to play again.");
	}

	@After
	public void disconnect() {
		node.getNotifications().detach(player1);
		node2.getNotifications().detach(player2);
		log.debug("listeners diconnected");
	}

	public static void quit(String reason) {
		node.getNotifications().shutdown();
		node2.getNotifications().shutdown();
		fail(reason);
		throw new RuntimeException();
	}

	public static void quit() { quit(""); }
	public static void quit(Throwable e) { quit(e.getMessage()); }

}
