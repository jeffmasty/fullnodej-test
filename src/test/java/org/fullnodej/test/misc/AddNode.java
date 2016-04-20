package org.fullnodej.test.misc;

import static org.junit.Assert.assertTrue;

import org.fullnodej.data.AddedNodeInfo;
import org.fullnodej.data.PeerInfo;
import org.fullnodej.test.FullnodeTest;
import org.junit.Test;

public class AddNode extends FullnodeTest {

	final String addnode = data.addnode;

	@Test public void test() {

		if (notSet(data.addnode)) {
			skipped("data.addnode not set");
			return;
		}
		boolean added = reportAdded();

		if (added) {
			log.info("removing...");
			rpc.addnode(addnode, "remove");
			reportAdded();
		}

		log.info("adding...");
		rpc.addnode(addnode, "add");
		assertTrue(reportAdded());

	}

	/**Logs connected and addnode status
	 * @return isAdded */
	boolean reportAdded() {
		boolean connected = isPeer();
		boolean added = isAdded();
		log.info("[" + addnode + "] connected:" + connected + " added:" + added + " peers:" + rpc.getconnectioncount());
		return added;
	}

	boolean isPeer() {
		for (PeerInfo peer : rpc.getpeerinfo())
			if (peer.getAddr().equals(addnode))
				return true;
		return false;
	}

	boolean isAdded() {
		for (AddedNodeInfo info :rpc.getaddednodeinfo(true))
			if (info.getAddednode().equals(addnode))
				return true;
		return false;
	}
}
