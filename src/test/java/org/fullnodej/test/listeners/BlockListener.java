package org.fullnodej.test.listeners;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.fullnodej.Notify;
import org.fullnodej.data.Block;
import org.fullnodej.exception.ConfigurationException;
import org.junit.Test;

/**Listens for data.confirmations # of blocks coming over the bitcoind notify interface*/
public class BlockListener extends NotifyAdapter implements Notify {

	private CountDownLatch countDown;
	private int targetHeight;
	private Block newBlock;

	@Test
	public void test() {

		if (data.confirmations == 0) {
			skipped();
			return;
		}

		int current = rpc.getblockcount();
		targetHeight = current + data.confirmations;
		boolean success = false;
		try {
			node.getNotifications().attach(this);
			log.warn("currently at " + current + ", I await() on block " + targetHeight + "...");
			countDown = new CountDownLatch(data.confirmations);
			success = countDown.await(3, TimeUnit.HOURS);
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (ConfigurationException e) {
			fail("Fullnode Configuration: " + e.getMessage());
		} catch (InterruptedException e) {
			fail("inturrupt: " + e.getMessage());
		}
		assertTrue("timed out", success);
		assertEquals(targetHeight, newBlock.getHeight());
		passed("Block " + targetHeight + " received containing " + newBlock.getTx().size() + " transactions.");
	}

	@Override
	public void blockNotify(Block block) {
		log.info("block received: " + block.getHeight());
		newBlock = block;
		countDown.countDown();
	}

}
