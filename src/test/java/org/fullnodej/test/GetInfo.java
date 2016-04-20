package org.fullnodej.test;

import static org.junit.Assert.*;

import java.util.Collection;

import org.fullnodej.data.BlockchainInfo;
import org.fullnodej.data.MempoolInfo;
import org.fullnodej.data.MiningInfo;
import org.fullnodej.data.NetworkInfo;
import org.fullnodej.data.NetworkTotals;
import org.fullnodej.data.PeerInfo;
import org.junit.Test;


/** Get Info: BlockchainInfo, MempoolInfo, MiningInfo, NetworkInfo, NetTotals, PeerInfo, WalletInfo, */
public class GetInfo extends FullnodeTest {

	@Test public void test() {

		assertNotNull(rpc.getinfo());
		assertTrue(rpc.getinfo().getRelayfee().floatValue() > 0f);

		BlockchainInfo chain = rpc.getblockchaininfo();
		if (chain.getBlocks() > chain.getHeaders()) fail();

		MempoolInfo mempool = rpc.getmempoolinfo();
		if (mempool.getSize() > 0) assertTrue(mempool.toString(), mempool.getBytes() > 10 * mempool.getSize());
		else log.info("No transactions in mempool");

		MiningInfo mining = rpc.getmininginfo();
		if (mining.getErrors() != null) log.info("MiningInfo: " + mining.getErrors());

		NetworkInfo network = rpc.getnetworkinfo();
		assertTrue(network.getRelayfee().floatValue() > 0f);

		NetworkTotals nettotals = rpc.getnettotals();
		assertTrue(nettotals.toString(), nettotals.getTotalbytesrecv() > 0); // the rpc has received at least 1 p2p byte since startup

		Collection<PeerInfo> peers = rpc.getpeerinfo();
		assertFalse(peers.isEmpty());
		for (PeerInfo peer : peers)
			if (peer.getBanscore() > 0) {
				assertTrue(peer.toString(), peer.getBanscore() <= 100);
				log.warn("Misbehaving Peer?: " + peer);
			}

		assertNotNull(rpc.getwalletinfo());

		assertFalse(rpc.help().isEmpty());
		assertFalse(rpc.help("getblockheight").isEmpty());

		passed(chain.getChain() + " Height:" + rpc.getblockcount() +
				" Difficulty:" + rpc.getdifficulty() +
				" NetHash/s:" + rpc.getnetworkhashps(5, -1));
	}

}
