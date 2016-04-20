package org.fullnodej.test.misc;

import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.fullnodej.data.PeerInfo;
import org.fullnodej.test.FullnodeTest;
import org.junit.Test;



/** ping, sleep 10 seconds, check peerinfo for ping times */
public class PingNodes extends FullnodeTest {

	int waitSecs = 3;

	@Test
	public void test() {
		boolean pingNeeded = false;
		for (PeerInfo peer : rpc.getpeerinfo()) {
			if (peer.getPingtime() == null || peer.getPingtime().compareTo(BigDecimal.ZERO) == 0) {
				pingNeeded = true;
				break;
			}
		}

		if (pingNeeded) log.info("ping() needed (" + waitSecs + " secs...)");
		else log.info("ping() not needed but altering the universe anyways (" + waitSecs + " secs...)");
		rpc.ping();
		try {
			Thread.sleep(1000 * waitSecs);
		} catch (InterruptedException e) { fail(e.getMessage()); }

		pingNeeded = false;
		for (PeerInfo peer : rpc.getpeerinfo()) {
			log.info(peer.getPingtime() + " " + peer.getAddr() + " " +
					(peer.getPingwait() == null ? "" : "**" + peer.getPingwait() + "**"));
			if (peer.getPingtime() == null || peer.getPingtime().compareTo(BigDecimal.ZERO) == 0)
				pingNeeded = true; // not good
		}
		if (pingNeeded) fail("ping time not set");
		passed("*the more you know...");
	}

}
